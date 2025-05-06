package com.hogumiwarts.lumos.utils.uwb

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbComplexChannel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.UUID

class GattConnector(
    private val context: Context,
    private val uwbRangingManager: UwbRangingManager
) {
    companion object {
        private const val TAG = "BleGattConnector"
        private const val GATT_OPERATION_TIMEOUT = 10_000L

        // UWB 서비스·특성 UUID (SmartTag2 사양에 맞게 수정 필요)
        private val UWB_SERVICE_UUID = UUID.fromString("eedd5e73-6aa8-4673-8219-398a489da87c")
        private val UWB_CHANNEL_CHARACTERISTIC_UUID = UUID.fromString("50f98bfd-158c-4efa-add4-0a70c2f5df5d")
        private val UWB_SESSION_KEY_CHARACTERISTIC_UUID = UUID.fromString("a12be31c-5b38-4773-9b9d-3d5735233a7c")
        private val UWB_DEVICE_ADDRESS_CHARACTERISTIC_UUID = UUID.fromString("cb91b0d6-3080-dbfb-876b-407db045e52b")
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private var isConnecting = false
    private var isConnected = false

    private var connectionDeferred: CompletableDeferred<Boolean>? = null
    private var serviceDiscoveryDeferred: CompletableDeferred<Boolean>? = null
    private var characteristicReadDeferred: CompletableDeferred<ByteArray?>? = null

    private fun exploreGattServices(gatt: BluetoothGatt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
            return
        }
        val services = try {
            gatt.services
        } catch (e: SecurityException) {
            Log.e(TAG, "권한 부족으로 서비스 목록을 가져올 수 없습니다: ${e.message}")
            return
        }
        Log.d(TAG, "사용 가능한 서비스 수: ${services.size}")
        for (service in services) {
            Log.d(TAG, "서비스: ${service.uuid}")
            service.characteristics.forEach { characteristic ->
                val props = characteristic.properties
                val sb = StringBuilder()
                if ((props and BluetoothGattCharacteristic.PROPERTY_READ) > 0) sb.append("READ ")
                if ((props and BluetoothGattCharacteristic.PROPERTY_WRITE) > 0) sb.append("WRITE ")
                if ((props and BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) sb.append("NOTIFY ")
                if ((props and BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) sb.append("INDICATE ")
                Log.d(TAG, "  특성: ${characteristic.uuid}, 속성: $sb")
                characteristic.descriptors.forEach { descriptor ->
                    Log.d(TAG, "    디스크립터: ${descriptor.uuid}")
                }
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d(TAG, "Connected to GATT server.")
                        isConnected = true; isConnecting = false
                        connectionDeferred?.complete(true)
                        coroutineScope.launch {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
                                return@launch
                            }
                            try { gatt.discoverServices() }
                            catch (e: SecurityException) { serviceDiscoveryDeferred?.complete(false) }
                        }
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(TAG, "Disconnected from GATT server.")
                        isConnected = false; isConnecting = false
                        connectionDeferred?.complete(false)
                        closeGatt()
                    }
                }
            } else {
                Log.w(TAG, "GATT connection failed status: $status")
                isConnected = false; isConnecting = false
                connectionDeferred?.complete(false)
                closeGatt()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "GATT 서비스 탐색 성공.")
                exploreGattServices(gatt)
                serviceDiscoveryDeferred?.complete(true)
            } else {
                Log.w(TAG, "서비스 탐색 실패: $status")
                serviceDiscoveryDeferred?.complete(false)
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic read 성공: ${characteristic.uuid}")
                characteristicReadDeferred?.complete(value)
            } else {
                Log.w(TAG, "Characteristic read 실패: $status")
                characteristicReadDeferred?.complete(null)
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val value = try { characteristic.value } catch (_: SecurityException) { null }
                characteristicReadDeferred?.complete(value)
            } else {
                characteristicReadDeferred?.complete(null)
            }
        }
    }

    suspend fun connectAndRetrieveOobParameters(device: BluetoothDevice): Boolean {
        if (isConnecting || isConnected) return false
        return try {
            isConnecting = true
            if (!connectToDevice(device)) return false
            if (!discoverServices()) return false

            val uwbService = bluetoothGatt?.getService(UWB_SERVICE_UUID)
                ?: return false.also { Log.e(TAG, "UWB service not found for ${device.address}") }

            val channelData = readCharacteristic(uwbService, UWB_CHANNEL_CHARACTERISTIC_UUID)
                ?: return false.also { Log.e(TAG, "채널 특성 읽기 실패.") }

            val sessionKeyData = readCharacteristic(uwbService, UWB_SESSION_KEY_CHARACTERISTIC_UUID)
                ?.takeIf { it.size == 8 }
                ?: return false.also { Log.e(TAG, "세션 키 읽기 실패 or invalid length.") }

            val deviceAddressData = readCharacteristic(uwbService, UWB_DEVICE_ADDRESS_CHARACTERISTIC_UUID)
            val addressBytes = deviceAddressData
                ?: throw IllegalStateException("OOB 파라미터에 주소 정보가 없습니다.")
            val uwbAddress = UwbAddress(addressBytes)

            val channel = parseChannelData(channelData)

            uwbRangingManager.addOobParameter(
                address        = uwbAddress,
                complexChannel = channel,
                sessionKeyInfo = sessionKeyData
            )

            Log.d(TAG, "OOB parameters added for UWB address: $uwbAddress")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving OOB parameters: ${e.message}")
            false
        } finally {
            closeGatt()
            isConnecting = false
            isConnected = false
        }
    }

    private suspend fun connectToDevice(device: BluetoothDevice): Boolean = withContext(Dispatchers.IO) {
        connectionDeferred = CompletableDeferred()
        bluetoothGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
            return@withContext false
        } else device.connectGatt(context, false, gattCallback)

        return@withContext try {
            withTimeout(GATT_OPERATION_TIMEOUT) { connectionDeferred?.await() ?: false }
        } catch (_: TimeoutCancellationException) {
            Log.e(TAG, "GATT 연결 타임아웃")
            false
        }
    }

    private suspend fun discoverServices(): Boolean = withContext(Dispatchers.IO) {
        serviceDiscoveryDeferred = CompletableDeferred()
        return@withContext try {
            withTimeout(GATT_OPERATION_TIMEOUT) { serviceDiscoveryDeferred?.await() ?: false }
        } catch (_: TimeoutCancellationException) {
            Log.e(TAG, "서비스 탐색 타임아웃")
            false
        }
    }

    private suspend fun readCharacteristic(
        service: BluetoothGattService,
        characteristicUuid: UUID
    ): ByteArray? = withContext(Dispatchers.IO) {
        val characteristic = service.getCharacteristic(characteristicUuid)
            ?: return@withContext null.also { Log.e(TAG, "Characteristic not found: $characteristicUuid") }

        characteristicReadDeferred = CompletableDeferred()

        // 1. 권한 체크
        val canRead = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) ==
                    PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
        if (!canRead) {
            Log.e(TAG, "BLUETOOTH_CONNECT 권한 없음")
            return@withContext null
        }

        // 2. 실제 읽기 시도 (SecurityException 방어)
        val readStarted = try {
            bluetoothGatt?.readCharacteristic(characteristic) == true
        } catch (e: SecurityException) {
            Log.e(TAG, "Characteristic read 호출 중 SecurityException", e)
            false
        }
        if (!readStarted) {
            Log.e(TAG, "Characteristic read 요청 실패")
            return@withContext null
        }

        // 3. 결과 대기
        return@withContext try {
            withTimeout(GATT_OPERATION_TIMEOUT) { characteristicReadDeferred?.await() }
        } catch (_: TimeoutCancellationException) {
            Log.e(TAG, "특성 읽기 타임아웃")
            null
        }
    }


    private fun parseChannelData(data: ByteArray): UwbComplexChannel {
        val channel = data[0].toInt() and 0xFF
        val prf     = data[1].toInt() and 0xFF
        return UwbComplexChannel(channel = channel, preambleIndex = prf)
    }

    private fun closeGatt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}
