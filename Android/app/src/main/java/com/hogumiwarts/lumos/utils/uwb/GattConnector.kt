package com.hogumiwarts.lumos.utils.uwb

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume

class GattConnector @Inject constructor(
    @ApplicationContext private val context: Context,
    private val uwbParamsRepository: UwbParamsRepository
) {
    companion object {
        private const val TAG = "BleGattConnector"
        private const val GATT_OPERATION_TIMEOUT = 10_000L

        // TODO: 실제 DWM3001-CDK 서비스·특성 UUID 로 교체!
        val UWB_SERVICE_UUID: UUID = UUID.fromString("0000FC00-0000-1000-8000-00805F9B34FB")
        val ADDR_CHAR_UUID: UUID = UUID.fromString("0000FC01-0000-1000-8000-00805F9B34FB")
        val CHANNEL_CHAR_UUID: UUID = UUID.fromString("0000FC02-0000-1000-8000-00805F9B34FB")
        val STS_KEY_CHAR_UUID: UUID = UUID.fromString("0000FC03-0000-1000-8000-00805F9B34FB")
    }

    private var bluetoothGatt: BluetoothGatt? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // 연결 상태 및 파라미터 값 StateFlow
    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    private val _uwbParams = MutableStateFlow<UwbParams?>(null)
    val uwbParams: StateFlow<UwbParams?> = _uwbParams

    // 특성 읽기를 위한 CancellableContinuation 맵
    private val charReadContinuations = ConcurrentHashMap<UUID, CancellableContinuation<Boolean>>()

    // 연결 상태 정의
    enum class ConnectionState {
        DISCONNECTED, CONNECTING, CONNECTED, SERVICES_DISCOVERED, READY, FAILED
    }

    // 연결 및 파라미터 획득 메서드
    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice, viewModelScope: CoroutineScope): Job = scope.launch {
        if (_connectionState.value != ConnectionState.DISCONNECTED) {
            disconnect()
        }

        // 저장된 파라미터가 있는지 확인
        val savedParams = uwbParamsRepository.loadUwbParams(device.address)
        if (savedParams != null) {
            Log.d(TAG, "저장된 UWB 파라미터 사용: ${device.address}")
            _uwbParams.value = savedParams
            _connectionState.value = ConnectionState.READY
            return@launch
        }

        _connectionState.value = ConnectionState.CONNECTING
        Log.d(TAG, "기기 연결 시작: ${device.address}")

        try {
            // BluetoothGatt 콜백 생성
            val gattCallback = createGattCallback()

            // 기기 연결
            withContext(Dispatchers.Main) {
                bluetoothGatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
            }

            // 타임아웃 처리
            withTimeoutOrNull(GATT_OPERATION_TIMEOUT) {
                // 연결 완료 대기
                _connectionState.first { it == ConnectionState.CONNECTED }

                // 서비스 발견 대기
                _connectionState.first { it == ConnectionState.SERVICES_DISCOVERED }

                // UWB 파라미터 읽기
                readUwbParameters()

                // 준비 상태 대기
                _connectionState.first { it == ConnectionState.READY }
            } ?: run {
                Log.e(TAG, "GATT 연결 타임아웃")
                _connectionState.value = ConnectionState.FAILED
                disconnect()
            }
        } catch (e: Exception) {
            Log.e(TAG, "GATT 연결 오류: ${e.message}")
            _connectionState.value = ConnectionState.FAILED
            disconnect()
        }
    }

    // GATT 콜백 생성
    @SuppressLint("MissingPermission")
    private fun createGattCallback(): BluetoothGattCallback {
        return object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            Log.d(TAG, "기기 연결됨: ${gatt.device.address}")
                            _connectionState.value = ConnectionState.CONNECTED

                            // 서비스 검색 시작
                            gatt.discoverServices()
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Log.d(TAG, "기기 연결 해제됨: ${gatt.device.address}")
                            _connectionState.value = ConnectionState.DISCONNECTED
                            disconnect()
                        }
                    }
                } else {
                    Log.e(TAG, "연결 상태 변경 오류: $status")
                    _connectionState.value = ConnectionState.FAILED
                    disconnect()
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Log.d(TAG, "서비스 발견 완료")
                    _connectionState.value = ConnectionState.SERVICES_DISCOVERED
                } else {
                    Log.e(TAG, "서비스 발견 실패: $status")
                    _connectionState.value = ConnectionState.FAILED
                }
            }

            // API 33 이상용 (Android 13+)
            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
                status: Int
            ) {
                handleCharacteristicRead(gatt, characteristic, value, status)
            }

            // API 33 미만용 (Android 12 이하)
            @Suppress("DEPRECATION")
            override fun onCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                status: Int
            ) {
                val value = characteristic.value ?: ByteArray(0)
                handleCharacteristicRead(gatt, characteristic, value, status)
            }

            // 특성 읽기 결과 처리 공통 메서드
            private fun handleCharacteristicRead(
                gatt: BluetoothGatt,
                characteristic: BluetoothGattCharacteristic,
                value: ByteArray,
                status: Int
            ) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    when (characteristic.uuid) {
                        ADDR_CHAR_UUID -> {
                            Log.d(TAG, "UWB 주소 읽기 성공: ${formatBytesToHex(value)}")
                            updateUwbParam { copy(uwbAddress = value) }
                        }
                        CHANNEL_CHAR_UUID -> {
                            val channel = value[0].toInt() and 0xFF
                            Log.d(TAG, "UWB 채널 읽기 성공: $channel")
                            updateUwbParam { copy(channel = channel) }
                        }
                        STS_KEY_CHAR_UUID -> {
                            Log.d(TAG, "STS 키 읽기 성공: ${formatBytesToHex(value)}")
                            updateUwbParam { copy(stsKey = value) }
                        }
                    }
                } else {
                    Log.e(TAG, "특성 읽기 실패: $status")
                }

                // 코루틴 resume
                val continuation = charReadContinuations.remove(characteristic.uuid)
                continuation?.resume(status == BluetoothGatt.GATT_SUCCESS)
            }
        }
    }

    // 바이트 배열을 16진수 문자열로 변환하는 유틸리티 메서드
    private fun formatBytesToHex(bytes: ByteArray): String {
        return bytes.joinToString(":") { "%02X".format(it) }
    }

    // UWB 파라미터 업데이트 유틸리티 메서드
    private fun updateUwbParam(update: UwbParams.() -> UwbParams) {
        val current = _uwbParams.value
        if (current != null) {
            _uwbParams.value = current.update()
        }
    }

    // UWB 파라미터 읽기
    @SuppressLint("MissingPermission")
    private suspend fun readUwbParameters() {
        val gatt = bluetoothGatt ?: return

        // UWB 서비스 찾기
        val uwbService = gatt.getService(UWB_SERVICE_UUID)
        if (uwbService == null) {
            Log.e(TAG, "UWB 서비스를 찾을 수 없음")
            _connectionState.value = ConnectionState.FAILED
            return
        }

        // 초기 파라미터 객체 생성
        _uwbParams.value = UwbParams(
            uwbAddress = ByteArray(8),
            channel = 0,
            stsKey = ByteArray(8)
        )

        // 특성 읽기
        val addrSuccess = readCharacteristic(gatt, uwbService, ADDR_CHAR_UUID)
        val channelSuccess = readCharacteristic(gatt, uwbService, CHANNEL_CHAR_UUID)
        val stsKeySuccess = readCharacteristic(gatt, uwbService, STS_KEY_CHAR_UUID)

        if (addrSuccess && channelSuccess && stsKeySuccess) {
            // 모든 데이터 읽기 완료 확인
            val params = _uwbParams.value
            if (params != null &&
                params.uwbAddress.isNotEmpty() &&
                params.channel > 0 &&
                params.stsKey.isNotEmpty()
            ) {
                // 파라미터 저장
                uwbParamsRepository.saveUwbParams(gatt.device.address, params)
                _connectionState.value = ConnectionState.READY
            } else {
                Log.e(TAG, "UWB 파라미터 읽기 실패: 불완전한 데이터")
                _connectionState.value = ConnectionState.FAILED
            }
        } else {
            Log.e(TAG, "UWB 파라미터 읽기 실패")
            _connectionState.value = ConnectionState.FAILED
        }
    }

    // 특성 읽기 유틸리티 메서드
    @SuppressLint("MissingPermission")
    private suspend fun readCharacteristic(
        gatt: BluetoothGatt,
        service: BluetoothGattService,
        charUuid: UUID
    ): Boolean = suspendCancellableCoroutine { cont ->
        val characteristic = service.getCharacteristic(charUuid)
        if (characteristic == null) {
            Log.e(TAG, "특성을 찾을 수 없음: $charUuid")
            cont.resume(false)
            return@suspendCancellableCoroutine
        }

        // ConcurrentHashMap에 Continuation 저장
        charReadContinuations[charUuid] = cont

        cont.invokeOnCancellation {
            charReadContinuations.remove(charUuid)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.readCharacteristic(characteristic)
        } else {
            @Suppress("DEPRECATION")
            val result = gatt.readCharacteristic(characteristic)
            if (!result) {
                // 요청 실패 시 즉시 resume
                charReadContinuations.remove(charUuid)
                cont.resume(false)
            }
            // 성공 시 onCharacteristicRead에서 resume
        }
    }

    // 연결 해제
    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.let { gatt ->
            gatt.disconnect()
            gatt.close()
        }
        bluetoothGatt = null
    }

    // 리소스 정리
    fun cleanup() {
        disconnect()
        scope.cancel()
    }
}
