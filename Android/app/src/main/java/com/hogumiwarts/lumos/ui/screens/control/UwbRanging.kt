package com.hogumiwarts.lumos.ui.screens.control

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.uwb.RangingMeasurement
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingPosition
import androidx.core.uwb.RangingResult
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbClientSessionScope
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbDevice
import androidx.core.uwb.UwbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UwbRanging @Inject constructor(private val uwbManager: UwbManager) {


    private lateinit var rangingJob: Job // 레인징 작업을 위한 코루틴 Job 객체
    private var clientSession: UwbClientSessionScope? = null // UWB 클라이언트 세션 범위

    // 클래스 레벨에서 세션 초기화 상태를 추적
    private var isSessionInitialized = false

    // 주소가 초기화되었는지 추적
    private var isAddressInitialized = false

    // 상태 변수들 - Compose UI에서 사용 가능하도록 mutableStateOf로 정의
    var localAdr by mutableStateOf("XX:XX") // 로컬 UWB 장치 주소
    var rangingActive by mutableStateOf(false) // 레인징 활성화 상태
    var rangingPosition by mutableStateOf(
        RangingPosition(
            RangingMeasurement(0F), // 거리 초기값
            RangingMeasurement(0F), // 방위각 초기값
            RangingMeasurement(0F), // 고도 초기값
            0L // 경과 시간 초기값
        )
    )

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
            clientSession = uwbManager.controllerSessionScope() // 컨트롤러 세션 스코프 생성

            // 주소가 아직 초기화되지 않은 경우에만 새 주소 할당
            if (!isAddressInitialized) {
                localAdr = clientSession?.localAddress.toString()
                isAddressInitialized = true
            }

            isSessionInitialized = true
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
     * 레인징을 시작하는 함수
     * @param remoteAdr 원격 UWB 장치 주소 (형식: "XX:XX")
     * @return 세션 시작 성공 여부
     */
    fun startRanging(remoteAdr: String): Boolean {
        if (clientSession == null) {
            Log.e("UwbRanging", "Session not initialized")
            return false // 세션이 준비되지 않았으면 실패 반환
        }
        // 이미 레인징이 활성화되어 있으면 중복 시작 방지
        if (rangingActive) {
            Log.d("UwbRanging", "Ranging already active")
            return true
        }

        try {
            val remoteUwbAdr = UwbAddress(remoteAdr)
            // 로그 추가로 디버깅
            Log.d("UwbRanging", "Starting ranging with remote address: $remoteAdr")

            // 레인징 매개변수는 유지
            val partnerParameters = RangingParameters(
                uwbConfigType = RangingParameters.CONFIG_UNICAST_DS_TWR, // 유니캐스트 양방향 레인징 설정
                // 세션 암호화를 위한 키 정보
                sessionKeyInfo = byteArrayOf(0x08, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06),
                complexChannel = UwbComplexChannel(9, 9), // README에 언급된 대로 Android 설정과 일치
                peerDevices = listOf(UwbDevice(remoteUwbAdr)), // 원격 장치 목록
                updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT, // 빈번한 업데이트 속도
                sessionId = 42, // 세션 ID
                subSessionId = 0, // 하위 세션 ID
                subSessionKeyInfo = null // 하위 세션 키 정보 (없음)
            )

            rangingJob = CoroutineScope(Dispatchers.Main.immediate).launch {
                try {
                    // 기존 세션 플로우 이미 사용 중인지 확인을 위한 로그
                    Log.d("UwbRanging", "Preparing session with parameters")

                    val sessionFlow = clientSession?.prepareSession(partnerParameters)

                    if (sessionFlow == null) {
                        Log.e("UwbRanging", "Failed to prepare session flow")
                        return@launch
                    }

                    Log.d("UwbRanging", "Starting to collect ranging results")

                    sessionFlow.collect {
                        when (it) {
                            is RangingResult.RangingResultPosition -> {
                                val distance = it.position.distance?.value ?: 0f
                                Log.d("UwbRanging", "Distance: $distance m")
                                rangingPosition = it.position
                            }
                            is RangingResult.RangingResultPeerDisconnected -> {
                                Log.d("UwbRanging", "Peer disconnected")
                                stopRanging()
                            }
                            else -> {
                                Log.d("UwbRanging", "Other ranging result: $it")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("UwbRanging", "Error during ranging: ${e.message}")
                    rangingActive = false

                    // 세션 재생성 시도 - 이 부분이 중요
                    cleanupSession()
                    prepareSession()
                }
            }

            rangingActive = true
            return true

        } catch (e: Exception) {
            Log.e("UwbRanging", "Error preparing ranging: ${e.message}")
            return false

        }
//
//        val remoteUwbAdr = UwbAddress(remoteAdr) // 원격 UWB 주소 객체 생성
//        // 레인징 매개변수 생성
//        val partnerParameters = RangingParameters(
//            uwbConfigType = RangingParameters.CONFIG_UNICAST_DS_TWR, // 유니캐스트 양방향 레인징 설정
//            // 세션 암호화를 위한 키 정보
//            sessionKeyInfo = byteArrayOf(0x08, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06),
//            complexChannel = UwbComplexChannel(9, 9), // README에 언급된 대로 Android 설정과 일치
//            peerDevices = listOf(UwbDevice(remoteUwbAdr)), // 원격 장치 목록
//            updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT, // 빈번한 업데이트 속도
//            sessionId = 42, // 세션 ID
//            subSessionId = 0, // 하위 세션 ID
//            subSessionKeyInfo = null // 하위 세션 키 정보 (없음)
//        )
//
//        // 레인징을 시작하는 코루틴 실행
//        rangingJob = CoroutineScope(Dispatchers.Main.immediate).launch {
//            try {
//                val sessionFlow = clientSession?.prepareSession(partnerParameters)
//
//                sessionFlow?.collect {
//                    when (it) {
//                        is RangingResult.RangingResultPosition -> {
//                            Log.d("collect", it.position.distance?.value.toString() + " m")
//                            rangingPosition = it.position
//                        }
//
//                        is RangingResult.RangingResultPeerDisconnected -> {
//                            Log.d("collect", "Peer disconnected")
//                            stopRanging()
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("UwbRanging", "Error during ranging: ${e.message}")
//                // 오류 발생 시 상태 초기화
//                stopRanging()
//                // 다시 세션 준비
//                cleanupSession()
//                prepareSession()
//            }
//        }
//        rangingActive = true // 레인징 활성화 상태로 설정
//        return true // 성공 반환
    }

    /**
     * 레인징을 중지하는 함수
     */
    fun stopRanging() {
        if (::rangingJob.isInitialized) { // rangingJob이 초기화되었는지 확인
            rangingActive = false // 레인징 비활성화 상태로 설정
            rangingJob.cancel() // 레인징 작업 취소
        }
    }

    /**
     * 애플리케이션 종료 시 세션 정리
     */
    fun cleanupSession() {
        if (isSessionInitialized) {
            if (rangingActive) {
                stopRanging()
            }
            clientSession = null
            isSessionInitialized = false
        }
    }

    /**
     * 앱이 완전히 종료될 때 모든 상태 초기화
     */
    fun cleanupAll() {
        cleanupSession()
        resetAddress()
    }
}