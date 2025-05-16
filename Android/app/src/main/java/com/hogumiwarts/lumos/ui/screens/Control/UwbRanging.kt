package com.hogumiwarts.lumos.ui.screens.control

import android.util.Log
import androidx.annotation.IntRange
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.uwb.RangingMeasurement
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingParameters.Companion.RANGING_SLOT_DURATION_1_MILLIS
import androidx.core.uwb.RangingParameters.Companion.RANGING_SLOT_DURATION_2_MILLIS
import androidx.core.uwb.RangingPosition
import androidx.core.uwb.RangingResult
import androidx.core.uwb.RangingResult.*
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbClientSessionScope
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbDevice
import androidx.core.uwb.UwbManager
import androidx.core.uwb.UwbRangeDataNtfConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UwbRanging @Inject constructor(private val uwbManager: UwbManager) {

    private lateinit var rangingJob: Job // 레인징 작업을 위한 코루틴 Job 객체
    private var clientSession: UwbClientSessionScope? = null // UWB 클라이언트 세션 범위

    // 여러 장치와의 세션을 관리하기 위한 맵
    private val deviceSessions = mutableMapOf<String, Job>()

    // 클래스 레벨에서 세션 초기화 상태를 추적
    private var isSessionInitialized = false

    // 주소가 초기화되었는지 추적
    private var isAddressInitialized = false

    // 상태 변수들 - Compose UI에서 사용 가능하도록 mutableStateOf로 정의
    var localAdr by mutableStateOf("XX:XX") // 로컬 UWB 장치 주소
    var rangingActive by mutableStateOf(false) // 레인징 활성화 상태

    // 멀티 장치 측정 결과를 저장할 맵
    var rangingPositions by mutableStateOf(mapOf<String, RangingPosition>())

    // 고정된 컨트롤리 주소 목록
    private val controleeAddresses = listOf("00:01")

    var rangingPosition by mutableStateOf(
        RangingPosition(
            RangingMeasurement(0F), // 거리 초기값
            RangingMeasurement(0F), // 방위각 초기값
            RangingMeasurement(0F), // 고도 초기값
            0L // 경과 시간 초기값
        )
    )

    // 세션 준비 여부
    var sessionReady by mutableStateOf(false)

    /**
     * 세션을 준비하는 함수
     * @param controller 이 장치가 컨트롤러인지 컨트롤리인지 여부
     */
    fun prepareSession() {
        // 레인징이 활성화된 상태에서는 세션을 새로 준비하지 않음
        if (rangingActive) {
            return
        }

        // 세션이 이미 초기화되어 있다면 먼저 정리
        if (isSessionInitialized) {
            cleanupSession()
        }

        CoroutineScope(Dispatchers.Main.immediate).launch {

            try {
                // Session 생성 시 로그 추가
                Log.d("UwbRanging", "Creating controller session scope")

                // 컨트롤러 세션 스코프 생성
                clientSession = uwbManager.controllerSessionScope()

                if (clientSession == null) {
                    Log.e("UwbRanging", "Failed to create controller session")
                    return@launch
                }

                // 주소가 아직 초기화되지 않은 경우에만 새 주소 할당
                if (!isAddressInitialized) {
                    localAdr = clientSession?.localAddress.toString()
                    Log.d("UwbRanging", "Local address initialized: $localAdr")
                    isAddressInitialized = true
                }

                isSessionInitialized = true
                sessionReady = true
                Log.d("UwbRanging", "Session prepared successfully")
            } catch (e: Exception) {
                Log.e("UwbRanging", "Error preparing session: ${e.message}", e)
                sessionReady = false
                isSessionInitialized = false
            }

        }
    }

    /**
     * 주소를 강제로 초기화하는 함수
     */
    fun resetAddress() {
        isAddressInitialized = false
        localAdr = "XX:XX"
    }



    /**
     * 첫 번째 디바이스 레인징을 시작하는 함수
     */
    fun startSingleRanging(): Boolean {
        if (clientSession == null || !sessionReady) {
            Log.e("UwbRanging", "Session not initialized")
            return false
        }

        if (rangingActive) {
            return true
        }

        try {
            // 컨트롤리 UWB 디바이스 목록 생성 - 유효한 첫 번째 주소만 사용하는 방식으로 시도
            val firstDevice = UwbDevice(UwbAddress(controleeAddresses.first()))

            // 2. 모든 디바이스를 한 번에 사용하는 대신 한 개의 디바이스만 먼저 시도
            val uwbDevices = listOf(firstDevice)

            val partnerParameters = RangingParameters(
                uwbConfigType = RangingParameters.CONFIG_UNICAST_DS_TWR,
                sessionKeyInfo = byteArrayOf(0x08, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06),
                complexChannel = UwbComplexChannel(9, 9),
                peerDevices = uwbDevices,
                updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT,
                sessionId = 42,
                subSessionId = 0,
                subSessionKeyInfo = null
            )

            // 세션 준비 전에 rangingActive 설정
            rangingActive = true

            rangingJob = CoroutineScope(Dispatchers.Main.immediate).launch {
                try {
                    Log.d("UwbRanging", "Preparing session with parameters")
                    val sessionFlow = clientSession?.prepareSession(partnerParameters)

                    if (sessionFlow == null) {
                        Log.e("UwbRanging", "Session flow is null")
                        rangingActive = false
                        return@launch
                    }

                    Log.d("UwbRanging", "Session prepared successfully, collecting results...")
                    // 4. 여기서 약간의 지연 추가
                    delay(100)  // 100ms 지연으로 API 호출 사이에 시간 여유 추가

                    // 5. try-catch 블록으로 collect 호출 래핑
                    try {
                        sessionFlow.collect { result ->
                            when (result) {
                                is RangingResultPosition -> {
                                    // 장치 MAC 주소 추출
                                    val deviceAddress = result.device.address.toString()
                                    Log.d("UwbRanging", "Position update from: $deviceAddress")

                                    // 결과 저장 - 맵 업데이트
                                    rangingPositions =
                                        rangingPositions + (deviceAddress to result.position)
                                }

                                is RangingResultPeerDisconnected -> {
                                    val deviceAddress = result.device.address.toString()
                                    Log.d("UwbRanging", "Peer disconnected: $deviceAddress")

                                    // 연결 해제된 장치 결과 제거
                                    rangingPositions = rangingPositions - deviceAddress
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // 코루틴 취소는 정상 동작이므로 구분하여 처리
                        if (e is kotlinx.coroutines.CancellationException) {
                            Log.d("UwbRanging", "Ranging collection was cancelled normally")
                        } else {
                            Log.e("UwbRanging", "Error during collection: ${e.message}", e)
                            rangingActive = false
                        }
                    }


                } catch (e: Exception) {
                    Log.e("UwbRanging", "Ranging failed: ${e.message}", e)
                    // 오류 발생 시 rangingActive를 false로 설정
                    rangingActive = false

                    // 세션 재준비는 지연 실행
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(500)  // 0.5초 지연 후 세션 재설정
                        cleanupSession()
                        delay(500)  // 추가 지연
                        prepareSession()
                    }
                }
            }
            return true
        } catch (e: Exception) {
            Log.e("UwbRanging", "startMulti error", e)
            rangingActive = false
            return false
        }
    }


    /**
     * 레인징을 중지하는 함수
     */
    fun stopRanging() {
        rangingActive = false // 레인징 비활성화 상태로 설정

        if (::rangingJob.isInitialized) { // rangingJob이 초기화되었는지 확인
            rangingJob.cancel() // 레인징 작업 취소
        }
        // 결과 맵 초기화
        rangingPositions = emptyMap()
    }

    /**
     * 애플리케이션 종료 시 세션 정리
     */
    fun cleanupSession() {
        if (isSessionInitialized) {
            if (rangingActive) {
                stopRanging()
            }

            // 모든 장치 세션 정리
            stopAllDeviceSessions()

            clientSession = null
            isSessionInitialized = false
            sessionReady = false
        }
    }

    /**
     * 특정 장치 세션을 중지하는 함수
     */
    private fun stopDeviceSession(address: String) {
        val job = deviceSessions[address]
        if (job != null) {
            Log.d("UwbRanging", "Stopping session for device: $address")
            job.cancel()
            deviceSessions.remove(address)
        }
    }

    /**
     * 모든 장치 세션을 중지하는 함수
     */
    private fun stopAllDeviceSessions() {
        Log.d("UwbRanging", "Stopping all device sessions: ${deviceSessions.size} sessions")

        for (address in deviceSessions.keys.toList()) {
            stopDeviceSession(address)
        }

        deviceSessions.clear()
    }

    /**
     * 앱이 완전히 종료될 때 모든 상태 초기화
     */
    fun cleanupAll() {
        cleanupSession()
        resetAddress()
    }


    /**
     * 특정 장치의 레인징 결과 가져오기
     */
    fun getDevicePosition(address: String): RangingPosition? {
        return rangingPositions[address]
    }

    /**
     * 연결된 장치 주소 목록 반환
     */
    fun getConnectedDevices(): List<String> {
        return rangingPositions.keys.toList()
    }

    /**
     * 컨트롤리 주소 목록 반환
     */
    fun getControleeAddresses(): List<String> {
        return controleeAddresses
    }
}