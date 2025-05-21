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
import androidx.core.uwb.UwbControllerSessionScope
import androidx.core.uwb.UwbDevice
import androidx.core.uwb.UwbManager
import androidx.core.uwb.UwbRangeDataNtfConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class UwbRanging @Inject constructor(private val uwbManager: UwbManager) {

    companion object { private const val TAG = "class UwbRanging" }

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
    private val controleeAddresses = listOf("00:01", "00:02")

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
        Timber.tag(TAG).d("▶️ prepareSession()  rangingActive=$rangingActive, isSessionInitialized=$isSessionInitialized")

        if (rangingActive) {
            Timber.tag(TAG).i("⏭  레인징 활성 상태이므로 세션 재준비 생략")
            return
        }
        if (isSessionInitialized) cleanupSession()

        CoroutineScope(Dispatchers.Main.immediate).launch {
            try {
                Timber.tag(TAG).d("💠 controllerSessionScope() 생성 시도")
                clientSession = uwbManager.controllerSessionScope()

                if (clientSession == null) {
                    Timber.tag(TAG).e("❌ controllerSessionScope() 반환값이 null → 세션 준비 실패")
                    return@launch
                }

                if (!isAddressInitialized) {
                    localAdr = clientSession!!.localAddress.toString()
                    Timber.tag(TAG).d("📡 Local UWB Address = $localAdr")
                    isAddressInitialized = true
                }

                isSessionInitialized = true
                sessionReady = true
                Timber.tag(TAG).i("✅ Session prepared  (sessionReady=$sessionReady)")
            } catch (e: Exception) {
                sessionReady = false
                isSessionInitialized = false
                Timber.tag(TAG).e(e, "🔥 Session 준비 중 예외 발생")
            }
        }
    }

    fun getLocalAddress(): String {
        return localAdr
    }

    fun isSessionReady(): Boolean {
        return sessionReady
    }

    // 선택적: 필요하다면 세션 객체 자체에 대한 접근자도 제공
    fun getClientSession(): UwbClientSessionScope? {
        return clientSession
    }

    /**
     * 주소를 강제로 초기화하는 함수
     */
    fun resetAddress() {
        this.isAddressInitialized = false
        localAdr = "XX:XX"
    }

    /**
     * 첫 번째 디바이스 레인징을 시작하는 함수
     */
    fun startSingleRanging(): Boolean {
        Timber.tag(TAG).d("▶️ startSingleRanging()  sessionReady=$sessionReady, rangingActive=$rangingActive")

        // 세션 준비 안 됐으면 바로 리턴
        if (clientSession == null || !sessionReady) {
            Timber.tag(TAG).w("⚠️  세션이 초기화되지 않음 → startSingleRanging() 중단")
            return false
        }
        if (rangingActive) {
            Timber.tag(TAG).i("⏩ 이미 레인징 중 – 중복 호출 무시")
            return true
        }

        return try {
            /* 1) 파라미터 구성 */
            val params = RangingParameters(
                uwbConfigType = RangingParameters.CONFIG_UNICAST_DS_TWR,
                sessionKeyInfo = byteArrayOf(0x08, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06),
                complexChannel = UwbComplexChannel(9, 9),
                peerDevices = listOf(
//                    UwbDevice(UwbAddress("00:01")),
                    UwbDevice(UwbAddress("00:02"))
                ),
                updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT,
                sessionId = 42,
                subSessionId = 0,
                subSessionKeyInfo = null
            )

            /* 2) 레인징 수집 코루틴 */
            rangingActive = true
            rangingJob = CoroutineScope(Dispatchers.Main.immediate).launch {
                Timber.tag(TAG).d("⌛ prepareSession().collect() 시작")
                try {
                    clientSession!!.prepareSession(params).collect { result ->

                        Timber.d(
                            "[collect 시작 / peer 정보] ${result.device.address}"
                        )
                        when (result) {
                            is RangingResultPosition -> {
                                val peer = result.device.address.toString()
                                Timber.d(
                                    "[peer 정보] ${peer}"
                                )
                                rangingPositions = rangingPositions + (peer to result.position)
                                Timber.tag(TAG).v("📍[$peer] dist=%.2f az=%.1f"
                                    .format(
                                        result.position.distance?.value ?: -1f,
                                        result.position.azimuth?.value ?: 0f
                                    )
                                )
                            }
                            is RangingResultPeerDisconnected -> {
                                val peer = result.device.address.toString()
                                rangingPositions = rangingPositions - peer
                                Timber.tag(TAG).w("🚫[$peer] Peer disconnected")
                            }
                        }
                    }
                } catch (ce: CancellationException) {
                    Timber.tag(TAG).d("🔄 레인징 collect 취소 (정상)")
                } catch (e: Exception) {
                    rangingActive = false
                    Timber.tag(TAG).e(e, "🔥 레인징 collect 중 예외")
                }
            }

            Timber.tag(TAG).i("🟢 레인징 시작 완료 (sessionId=42)")
            true
        } catch (e: Exception) {
            rangingActive = false
            Timber.tag(TAG).e(e, "🔥 startSingleRanging() 실패")
            false
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