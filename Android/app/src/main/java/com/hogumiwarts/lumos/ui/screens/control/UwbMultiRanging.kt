package com.hogumiwarts.lumos.ui.screens.control

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingPosition
import androidx.core.uwb.RangingResult.RangingResultPeerDisconnected
import androidx.core.uwb.RangingResult.RangingResultPosition
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbClientSessionScope
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbDevice
import androidx.core.uwb.UwbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UwbMultiRanging @Inject constructor(
    private val uwbManager: UwbManager,
    private val uwbRanging: UwbRanging
) {
    companion object {
        private const val TAG = "Multi Ranging"
    }

    // 멀티 장치 측정 결과를 저장할 맵
    var rangingPositions by mutableStateOf(mapOf<String, RangingPosition>())

    private val controleeAddresses = listOf("00:01", "00:02")

    // 로컬 주소는 UwbRanging에서 가져옴
    val localAdr get() = uwbRanging.getLocalAddress()
    var rangingActive by mutableStateOf(false) // 레인징 활성화 상태

    // 세션 준비 여부는 UwbRanging에서 가져옴
    val sessionReady get() = uwbRanging.isSessionReady()

    val clientSession get() = uwbRanging.getClientSession()

    // 장치별 레인징 작업
    private val rangingJobs = mutableMapOf<String, Job>()

    // 멀티 레인징
    private data class SessionHandle(
        val scope: UwbClientSessionScope?,
        val job: Job
    )

    private val sessions = mutableMapOf<String, SessionHandle>()

    private val _ranging = MutableStateFlow<Map<String, RangingPosition>>(emptyMap())
    val ranging: StateFlow<Map<String, RangingPosition>> = _ranging.asStateFlow()

    private val _pstsKeyHex = MutableStateFlow<String?>(null)
    val pstsKeyHex: StateFlow<String?> = _pstsKeyHex.asStateFlow()

    private var pstsKeyBytes: ByteArray? = null

//    private fun shortMacToBytes(str: String): ByteArray {
//        val bytes = str.split(":").map { it.toInt(16).toByte() }.toByteArray()
//        require(bytes.size == 2) { "UWB short address는 2바이트여야 합니다: $str" }
//        return bytes
//    }

    @Suppress("MissingPermission")
    fun startMultiRanging(): Boolean {

        if (rangingActive) {
            Timber.i("멀티 레인징 이미 활성 상태")
            return true
        }
        if (!sessionReady) {
            Timber.w("세션이 아직 준비되지 않음 → startMultiRanging() 중단")
            return false
        }
        // clientSession이 null이면 오류
        val session = clientSession ?: run {
            Timber.e("clientSession이 null - 멀티 레인징 시작 실패")
            return false
        }

        Timber.i("🔵 멀티 레인징 시작 — 컨트롤리 ${controleeAddresses.size}개")

        controleeAddresses.forEachIndexed { idx, macStr ->
//            startDeviceRanging(session, idx, macStr)

            CoroutineScope(Dispatchers.Main.immediate).launch {

                /* 1) 세션 스코프 생성 */
//                val scope = clientSession
                val scope = uwbManager.controllerSessionScope()
                Timber.d("[$macStr] 컨트롤러 세션 스코프 생성 완료")

                /* 2) 대상 디바이스 객체 */
                val controlee = UwbDevice(UwbAddress(macStr))

                /* 3) 세션 파라미터 */
                val params = RangingParameters(
                    uwbConfigType = RangingParameters.CONFIG_UNICAST_DS_TWR,
                    sessionKeyInfo = byteArrayOf(
                        0x08, 0x07, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06
                    )
//                        .map { it.toByte() }.toByteArray()
                    ,
                    complexChannel = UwbComplexChannel(9, 9),
                    peerDevices = listOf(controlee),
                    updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT,
                    sessionId = 42,
                    subSessionId = idx,
                    subSessionKeyInfo = null
                )
                Timber.d("[$macStr] 세션 파라미터 준비(sessionId=42, subSessionId=$idx)")

                /* 4) 결과 수집 Job */
                val innerJob = launch {
                    Timber.d("Job 실행")
                    try {
                        scope!!.prepareSession(params).collect { res ->
                            Timber.d(
                                "[peer 정보] ${res.device.address}"
                            )

                            when (res) {
                                is RangingResultPosition -> {
                                    val peer = res.device.address.toString()
                                    rangingPositions =
                                        rangingPositions + (peer to res.position)

                                    Timber.v(
                                        "[$peer] 거리=%.2f, 방위=%.1f"
                                            .format(
                                                res.position.distance?.value ?: -1f,
                                                res.position.azimuth?.value ?: 0f
                                            )
                                    )
                                }

                                is RangingResultPeerDisconnected -> {
                                    val peer = res.device.address.toString()
                                    rangingPositions = rangingPositions - peer
                                    Timber.w("[$peer] 🚫 연결 끊김")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "[$macStr] ❌ 레인징 수집 중 예외 발생")
                    }
                }

                Timber.i("[$macStr] ✅ 세션 시작 완료")
                sessions[macStr] =
                    SessionHandle(
                        scope,
                        innerJob
                    )
            }
        }

        rangingActive = true
        Timber.i("🟢 멀티 레인징 활성화 플래그 ON")
        return true
    }


    @Suppress("MissingPermission")
    fun startConfigMultiRanging(): Boolean {

        if (rangingActive) return true
        if (!sessionReady) return false
        Timber.i("🔵 멀티캐스트 시작: 로컬 주소=${localAdr}, 세션 준비=${sessionReady}")

        // 1) 컨트롤러 세션 스코프 하나
        val scope = clientSession
        Timber.i("🔵 세션 스코프: ${scope != null}")


        // 2) 컨트롤리 디바이스 객체 및 서브세션 파라미터 맵핑
//        val peers = mutableListOf<UwbDevice>()
        val peerDevices = mutableListOf<UwbDevice>()

        controleeAddresses.forEachIndexed { index, mac ->
            val device = UwbDevice(UwbAddress(mac))
            peerDevices.add(device)
        }


//        val masterKey = pstsKeyBytes ?: generateNewPstsKey()
        // 3) 16B P-STS 키 사용
        val masterKey = byteArrayOf(
            0x07,0x08,   // Vendor ID (big-endian)
            0x01, 0x02, 0x03, 0x04, 0x05, 0x06  // Static STS IV (6 bytes)
        )
//        val keyHex = masterKey.joinToString("") { "%02X".format(it) }
//        Timber.i("🔑 마스터 P-STS 키: $keyHex")


        // 4) 멀티캐스트 세션을 위한 RangingParameters 생성
        // Alpha 10에서는 Builder 패턴을 사용하여 파라미터를 구성할 수 있음
        val params = RangingParameters(
            uwbConfigType = RangingParameters.CONFIG_MULTICAST_DS_TWR,
            sessionKeyInfo = masterKey,
            complexChannel = UwbComplexChannel(9, 9),
            peerDevices = peerDevices,               // 빈 리스트로 시작
            updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT,
            sessionId = 42,
            subSessionId = 0,                  // 컨트롤러는 항상 0
            subSessionKeyInfo = null
        )

        // 상세 설정 로깅
        Timber.i("🔵 멀티캐스트 세션 상세 파라미터:")
        Timber.i("  - configType: ${params.uwbConfigType}")
        Timber.i("  - channel: ${params.complexChannel}")
        Timber.i("  - sessionId: ${params.sessionId}")
        Timber.i("  - subSessionId: ${params.subSessionId}")
        Timber.i("  - peerCount: ${peerDevices.size}")

        // 5) collect
        CoroutineScope(Dispatchers.Main.immediate).launch {
            try {
                Timber.i("🚀 멀티캐스트 세션 시작 시도...")
                scope!!.prepareSession(params).collect { res ->
                    when (res) {
                        is RangingResultPosition -> {
                            val addr = res.device.address.toString()
                            rangingPositions = rangingPositions + (addr to res.position)
                            Timber.i("📍[$addr] 위치: dist=${res.position.distance?.value}, az=${res.position.azimuth?.value}, el=${res.position.elevation?.value}")
                        }
                        is RangingResultPeerDisconnected -> {
                            val addr = res.device.address.toString()
                            rangingPositions = rangingPositions - addr
                            Timber.w("🚫[$addr] 연결 해제")
                        }
                        else -> {
                            Timber.d("🔄 기타 결과: $res")
                        }
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "🔥 멀티캐스트 collect 예외")
                rangingActive = false
            }
        }

        rangingActive = true
        Timber.i("✅ 멀티캐스트 세션 활성화 플래그 ON")
        return true
    }

    fun generateNewPstsKey(): ByteArray {
        val key = SecureRandom().generateSeed(16)
        pstsKeyBytes = key
        _pstsKeyHex.value = key.joinToString("") { "%02X".format(it) }
        Timber.i("새 P-STS 키 생성: ${_pstsKeyHex.value}")
        return key
    }

    private fun stopDeviceRanging(macStr: String) {
        rangingJobs[macStr]?.cancel()
        rangingJobs.remove(macStr)
        // 해당 장치 정보 제거
        rangingPositions = rangingPositions.filterKeys { it != macStr }
    }

    /* ------------ 세션 정리 ------------ */
    suspend fun stopAllRanging() {
        try {
            // 모든 레인징 작업 취소
            rangingJobs.values.forEach { it.cancel() }
            rangingJobs.clear()
            rangingPositions = emptyMap()
        } finally {
            rangingActive = false
        }
    }
}