package com.hogumiwarts.lumos.utils.uwb

import android.content.Context
import androidx.core.uwb.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.Executors

class UwbRangingManager(private val context: Context) {
//    companion object {
//        private const val TAG = "UwbRangingManager"
//    }
//
//    private lateinit var uwbManager: UwbManager
//    private var controllerSession: UwbControllerSessionScope? = null
//    private var rangingJob: Job? = null
//    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
//
//    // 멀티 태그 OOB 파라미터 목록
//    private data class TagInfo(
//        val address: UwbAddress,
//        val channel: UwbComplexChannel,
//        val sessionKey: ByteArray
//    )
//    private val tagInfos = mutableListOf<TagInfo>()
//    val isRanging: Boolean
//        get() = rangingJob?.isActive == true
//
//    // UWB 지원 확인
//    fun isUwbSupported(): Boolean {
//        return context.packageManager.hasSystemFeature("android.hardware.uwb")
//    }
//
//    // 초기화
//    fun initialize() {
//        if (!isUwbSupported()) {
//            Timber.tag(TAG).e("UWB is not supported on this device")
//            return
//        }
//        uwbManager = UwbManager.createInstance(context)
//    }
//
//    /*
//     * BLE GATT를 통해 읽은 OOB 파라미터를 설정
//     * @param address: UWB 대상 디바이스 주소
//     * @param complexChannel: UWB 채널/프리앰블 정보
//     * @param sessionKeyInfo: 8바이트 STATIC STS 세션 키
//     */
//    fun addOobParameter(
//        address: UwbAddress,
//        complexChannel: UwbComplexChannel,
//        sessionKeyInfo: ByteArray
//    ) {
//        if (sessionKeyInfo.size != 8) {
//            Timber.tag(TAG).e("Invalid sessionKeyInfo length: " + sessionKeyInfo.size)
//            return
//        }
//        // 멀티태그 시 같은 채널·키만 허용
//        if (tagInfos.isNotEmpty()) {
//            val first = tagInfos.first()
//            if (first.channel != complexChannel ||
//                !first.sessionKey.contentEquals(sessionKeyInfo)
//            ) {
//                Timber.tag(TAG).e("All tags must share same channel & key for multicast")
//                return
//            }
//        }
//        tagInfos.add(TagInfo(address, complexChannel, sessionKeyInfo))
//    }
//
//
//    /*
//     * 멀티캐스트 레인징 시작
//     * 모든 태그에 대해 한 세션에서 거리/방위각을 측정하고 콜백으로 전달
//     */
//    suspend fun startRanging(
//        onDistanceUpdate: (address: UwbAddress, distance: Float, azimuth: Float) -> Unit,
//        onError: (message: String) -> Unit
//    ) {
//        // OOB 파라미터 유효성 검증
//        if (tagInfos.isEmpty()) {
//            onError("Ranging할 태그 정보가 없습니다.")
//            return
//        }
//
//        // 이미 레인징 중이면 중지
//        if (rangingJob != null) stopRanging()
//
//        try {
//            // UWB 컨트롤러 세션 스코프 가져오기
//            controllerSession = uwbManager.controllerSessionScope()
//
//
//            // UwbDevice 리스트 생성
//            val peerDeviceList = tagInfos.map { info ->
//                UwbDevice.createForAddress(info.address.address)
//            }
//
//            // 레인징 파라미터 설정
//            val rangingParams = RangingParameters(
//                uwbConfigType = RangingParameters.CONFIG_MULTICAST_DS_TWR,
//                sessionId = 0,  // 세션 ID
//                subSessionId = 0,  // 서브 세션 ID
//                sessionKeyInfo = tagInfos[0].sessionKey,  // 세션 암호화 키 (불필요시 null)
//                subSessionKeyInfo = null,  // 서브 세션 암호화 키 (불필요시 null)
//                complexChannel = tagInfos[0].channel,  // UWB 채널 정보
//                peerDevices = peerDeviceList,  // 타겟 디바이스 목록
//                updateRateType = RangingParameters.RANGING_UPDATE_RATE_FREQUENT,  // 업데이트 속도, 정적 환경 + 낮은 빈도 요구 시
//            )
//
//
//            // 세션 준비 및 레인징 결과 Flow 수집
//            // Ranging 결과 수집
//
//
//            rangingJob = coroutineScope.launch {
//                controllerSession?.prepareSession(rangingParams)?.collect { result ->
//                    when (result) {
//                        is RangingResult.RangingResultPosition -> {
//                            // 거리 및 방위각 추출
//                            val addr = result.device.address
//                            val dist = result.position.distance?.value ?: 0f
//                            val az  = result.position.azimuth?.value  ?: 0f
//                            onDistanceUpdate(addr, dist, az)
//                        }
//                        is RangingResult.RangingResultPeerDisconnected -> {
//                            onError("Peer disconnected: ${result.device.address}")
//                        }
//                        is RangingResult.RangingResultInitialized -> {
//                            Timber.tag(TAG).d("Ranging initialized for " + result.device.address)
//                        }
//                        else -> { }
//                    }
//                }
//            }
//
//        } catch (e: Exception) {
//            Timber.tag(TAG).e("레인징 시작 오류: " + e.message)
//            onError(e.message ?: "Unknown error")
//        }
//    }
//
//    // 레인징 중지
//    fun stopRanging() {
//        rangingJob?.cancel()
//        rangingJob = null
//        controllerSession = null
//    }
//
//    // 기존 정보 초기화 (예: 태그 재스캔 전)
//    fun clearTagInfos() {
//        stopRanging()
//        tagInfos.clear()
//    }
}