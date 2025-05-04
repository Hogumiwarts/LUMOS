package com.hogumiwarts.lumos.utils.uwb

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.uwb.UwbComplexChannel
import com.hogumiwarts.lumos.utils.UwbRangingManager
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
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
        private const val GATT_OPERATION_TIMEOUT = 10000L

        // UWB 서비스와 특성 UUID (SmartTag2 사양에 맞게 수정 필요)
        // 참고: 이 값들은 예시이며 실제 SmartTag2의 UUID로 대체해야 함
        private val UWB_SERVICE_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")
        private val UWB_CHANNEL_CHARACTERISTIC_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb")
        private val UWB_SESSION_KEY_CHARACTERISTIC_UUID = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb")
        private val UWB_DEVICE_ADDRESS_CHARACTERISTIC_UUID = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb")
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    // 연결 상태 추적
    private var isConnecting = false
    private var isConnected = false

    // GATT 콜백 결과를 기다리기 위한 Deferred
    private var connectionDeferred: CompletableDeferred<Boolean>? = null
    private var serviceDiscoveryDeferred: CompletableDeferred<Boolean>? = null
    private var characteristicReadDeferred: CompletableDeferred<ByteArray?>? = null

    // GATT 콜백
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                when (newState) {
                    BluetoothProfile.STATE_CONNECTED -> {
                        Log.d(TAG, "Connected to GATT server.")
                        isConnected = true
                        isConnecting = false

                        // 연결 성공 알림
                        connectionDeferred?.complete(true)

                        // 서비스 탐색 시작
                        coroutineScope.launch {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
                                return@launch
                            }

                            try {
                                gatt.discoverServices()
                            } catch (e: SecurityException) {
                                Log.e(TAG, "권한 부족으로 서비스 탐색을 시작할 수 없습니다: ${e.message}")
                                serviceDiscoveryDeferred?.complete(false)
                            }
                        }
                    }
                    BluetoothProfile.STATE_DISCONNECTED -> {
                        Log.d(TAG, "Disconnected from GATT server.")
                        isConnected = false
                        isConnecting = false

                        // 연결 해제 알림
                        connectionDeferred?.complete(false)

                        // GATT 연결 닫기
                        closeGatt()
                    }
                }
            } else {
                Log.w(TAG, "GATT connection state change failed with status: $status")
                isConnected = false
                isConnecting = false

                // 연결 실패 알림
                connectionDeferred?.complete(false)

                // GATT 연결 닫기
                closeGatt()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "GATT services discovered.")

                // 서비스 탐색 성공 알림
                serviceDiscoveryDeferred?.complete(true)
            } else {
                Log.w(TAG, "GATT service discovery failed with status: $status")

                // 서비스 탐색 실패 알림
                serviceDiscoveryDeferred?.complete(false)
            }
        }

        // API 33 이상용 콜백 메서드
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "Characteristic read successful: ${characteristic.uuid}")

                // 특성 읽기 성공 알림
                characteristicReadDeferred?.complete(value)
            } else {
                Log.w(TAG, "Characteristic read failed with status: $status")

                // 특성 읽기 실패 알림
                characteristicReadDeferred?.complete(null)
            }
        }
    }

    // 기기에 연결하고 OOB 파라미터 획득
    suspend fun connectAndRetrieveOobParameters(device: BluetoothDevice): Boolean {
        if (isConnecting || isConnected) {
            Log.d(TAG, "Already connecting or connected.")
            return false
        }

        try {
            isConnecting = true

            // 기기 연결
            val connected = connectToDevice(device)
            if (!connected) {
                Log.e(TAG, "Failed to connect to device: ${device.address}")
                return false
            }

            // 서비스 탐색
            val servicesDiscovered = discoverServices()
            if (!servicesDiscovered) {
                Log.e(TAG, "Failed to discover services for device: ${device.address}")
                return false
            }

            // UWB 관련 특성 읽기
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
                return false
            }

            // UWB 서비스 찾기
            val uwbService = bluetoothGatt?.getService(UWB_SERVICE_UUID)
            if (uwbService == null) {
                Log.e(TAG, "UWB service not found.")
                return false
            }

            // UWB 채널 특성 읽기
            val channelData = readCharacteristic(uwbService, UWB_CHANNEL_CHARACTERISTIC_UUID)
            if (channelData == null) {
                Log.e(TAG, "Failed to read UWB channel characteristic.")
                return false
            }

            // UWB 세션 키 특성 읽기
            val sessionKeyData = readCharacteristic(uwbService, UWB_SESSION_KEY_CHARACTERISTIC_UUID)
            if (sessionKeyData == null || sessionKeyData.size != 8) {
                Log.e(TAG, "Failed to read UWB session key characteristic or invalid length.")
                return false
            }

            // UWB 디바이스 주소 특성 읽기 (옵션)
            val deviceAddressData = readCharacteristic(uwbService, UWB_DEVICE_ADDRESS_CHARACTERISTIC_UUID)
            val deviceAddress = deviceAddressData?.toString(Charsets.UTF_8) ?: device.address

            // 채널 데이터 파싱 (예시, 실제 구조에 맞게 수정 필요)
            val channel = parseChannelData(channelData)

            // UWB 레인징 매니저에 OOB 파라미터 추가
            uwbRangingManager.addOobParameter(
                address = deviceAddress,
                complexChannel = channel,
                sessionKeyInfo = sessionKeyData
            )

            Log.d(TAG, "Successfully retrieved and added OOB parameters for device: $deviceAddress")
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving OOB parameters: ${e.message}")
            return false
        } finally {
            // 연결 종료
            closeGatt()
            isConnecting = false
            isConnected = false
        }
    }

    // 기기에 연결
    private suspend fun connectToDevice(device: BluetoothDevice): Boolean {
        return withContext(Dispatchers.IO) {
            connectionDeferred = CompletableDeferred()

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
                    return@withContext false
                }

                bluetoothGatt = device.connectGatt(context, false, gattCallback)

                // 타임아웃 설정
                try {
                    withTimeout(GATT_OPERATION_TIMEOUT) {
                        connectionDeferred?.await() ?: false
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "GATT 연결 타임아웃")
                    return@withContext false
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "권한 부족으로 기기에 연결할 수 없습니다: ${e.message}")
                false
            } catch (e: Exception) {
                Log.e(TAG, "기기 연결 중 오류 발생: ${e.message}")
                false
            }
        }
    }


    // 서비스 탐색
    private suspend fun discoverServices(): Boolean {
        return withContext(Dispatchers.IO) {
            serviceDiscoveryDeferred = CompletableDeferred()

            // 타임아웃 설정
            try {
                withTimeout(GATT_OPERATION_TIMEOUT) {
                    serviceDiscoveryDeferred?.await() ?: false
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "서비스 탐색 타임아웃")
                return@withContext false
            }
        }
    }

    // 특성 읽기
    private suspend fun readCharacteristic(service: BluetoothGattService, characteristicUuid: UUID): ByteArray? {
        return withContext(Dispatchers.IO) {
            val characteristic = service.getCharacteristic(characteristicUuid)
            if (characteristic == null) {
                Log.e(TAG, "Characteristic not found: $characteristicUuid")
                return@withContext null
            }

            characteristicReadDeferred = CompletableDeferred()

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
                    return@withContext null
                }

                // Android 버전에 따라 다른 읽기 메소드 사용
                val readSuccess = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    bluetoothGatt?.readCharacteristic(characteristic) ?: false
                } else {
                    @Suppress("DEPRECATION")
                    bluetoothGatt?.readCharacteristic(characteristic) ?: false
                }

                if (!readSuccess) {
                    Log.e(TAG, "특성 읽기 요청 실패")
                    return@withContext null
                }

                // 타임아웃 설정
                try {
                    withTimeout(GATT_OPERATION_TIMEOUT) {
                        characteristicReadDeferred?.await()
                    }
                } catch (e: TimeoutCancellationException) {
                    Log.e(TAG, "특성 읽기 타임아웃")
                    return@withContext null
                }
            } catch (e: SecurityException) {
                Log.e(TAG, "권한 부족으로 특성을 읽을 수 없습니다: ${e.message}")
                null
            } catch (e: Exception) {
                Log.e(TAG, "특성 읽기 중 오류 발생: ${e.message}")
                null
            }
        }
    }

    // 채널 데이터 파싱 (예시, 실제 구조에 맞게 수정 필요)
    private fun parseChannelData(data: ByteArray): UwbComplexChannel {
        // 예시 코드: 실제 SmartTag2의 데이터 형식에 맞게 수정 필요
        // 데이터의 첫 번째 바이트를 채널 번호로 사용
        val channel = data[0].toInt() and 0xFF
        // 두 번째 바이트를 PRF(Pulse Repetition Frequency) 값으로 사용
        val prf = data[1].toInt() and 0xFF

        // UwbComplexChannel 객체 생성 (실제 파라미터 의미에 맞게 수정 필요)
        return UwbComplexChannel(
            channel = channel,
            preambleIndex = prf,
            // 기타 필요한 파라미터들
        )
    }

    // GATT 연결 종료
    private fun closeGatt() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "BLUETOOTH_CONNECT 권한이 없습니다.")
                return
            }

            bluetoothGatt?.close()
            bluetoothGatt = null
        } catch (e: SecurityException) {
            Log.e(TAG, "권한 부족으로 GATT 연결을 닫을 수 없습니다: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "GATT 연결 종료 중 오류 발생: ${e.message}")
        }
    }
}