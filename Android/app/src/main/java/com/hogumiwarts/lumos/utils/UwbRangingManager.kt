package com.hogumiwarts.lumos.utils

import android.content.Context
import android.util.Log
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingResult
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbControllerSessionScope
import androidx.core.uwb.UwbDevice
import androidx.core.uwb.UwbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UwbRangingManager(private val context: Context) {
    companion object {
        private const val TAG = "UwbRangingManager"
    }

    private lateinit var uwbManager: UwbManager
    private var uwbSessionScope: UwbControllerSessionScope? = null
    private var rangingJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var _isRanging = false
    val isRanging: Boolean get() = _isRanging

    // 멀티 태그 OOB 파라미터 목록
    private data class TagInfo(
        val address: String,
        val channel: UwbComplexChannel,
        val sessionKey: ByteArray
    )
    private val tagInfos = mutableListOf<TagInfo>()

    // UWB 지원 확인
    fun isUwbSupported(): Boolean {
        return context.packageManager.hasSystemFeature("android.hardware.uwb")
    }

    // 초기화
    fun initialize() {
        if (!isUwbSupported()) {
            Log.e(TAG, "UWB is not supported on this device")
            return
        }
        uwbManager = UwbManager.createInstance(context)
    }

    /*
     * BLE GATT를 통해 읽은 OOB 파라미터를 설정
     * @param address: UWB 대상 디바이스 주소
     * @param complexChannel: UWB 채널/프리앰블 정보
     * @param sessionKeyInfo: 8바이트 STATIC STS 세션 키
     */
    fun addOobParameter(
        address: String,
        complexChannel: UwbComplexChannel,
        sessionKeyInfo: ByteArray
    ) {
        if (sessionKeyInfo.size != 8) {
            Log.e(TAG, "Invalid sessionKeyInfo length: ${sessionKeyInfo.size}")
            return
        }
        tagInfos.add(TagInfo(address, complexChannel, sessionKeyInfo))
    }


    /*
     * 멀티캐스트 레인징 시작
     * 모든 태그에 대해 한 세션에서 거리/방위각을 측정하고 콜백으로 전달
     */
    suspend fun startRanging(
        onDistanceUpdate: (address: String, distance: Float, azimuth: Float) -> Unit,
        onError: (message: String) -> Unit
    ) {
        // OOB 파라미터 유효성 검증
        if (tagInfos.isEmpty()) {
            onError("레이징할 태그 정보가 없습니다.")
            return
        }
        // 채널과 키는 동일해야 함 (동일 프로필 가정)
        val channel = tagInfos[0].channel
        val sessionKey = tagInfos[0].sessionKey

        try {
            // 이미 레인징 중이면 중지
            if (_isRanging) {
                stopRanging()
            }

            // UWB 컨트롤러 세션 스코프 가져오기
            uwbSessionScope = uwbManager.controllerSessionScope()


            // UwbDevice 리스트 생성
            val peerDeviceList = tagInfos.map { UwbDevice.createForAddress(it.address) }

            // 레인징 파라미터 설정
            val rangingParams = RangingParameters(
                uwbConfigType = RangingParameters.CONFIG_MULTICAST_DS_TWR,
                sessionId = 0,  // 세션 ID
                subSessionId = 0,  // 서브 세션 ID
                sessionKeyInfo = sessionKey,  // 세션 암호화 키 (불필요시 null)
                subSessionKeyInfo = null,  // 서브 세션 암호화 키 (불필요시 null)
                complexChannel = channel,  // UWB 채널 정보
                peerDevices = peerDeviceList,  // 타겟 디바이스 목록
                updateRateType = RangingParameters.RANGING_UPDATE_RATE_AUTOMATIC,  // 업데이트 속도
                uwbRangeDataNtfConfig = null,  // 통지 설정 (기본값 사용)
                slotDurationMillis = RangingParameters.RANGING_SLOT_DURATION_2_MILLIS,  // 슬롯 지속 시간
                isAoaDisabled = false  // AoA(Angle of Arrival) 비활성화 여부
            )


            // 세션 준비 및 레인징 결과 Flow 수집
            // Ranging 결과 수집
            rangingJob = coroutineScope.launch {
                uwbSessionScope?.prepareSession(rangingParams)?.collect { result ->
                    when (result) {
                        is RangingResult.RangingResultPosition -> {
                            // 거리 및 방위각 추출
                            val addr = result.device.address
                            val dist = result.position.distance?.value ?: 0f
                            val az  = result.position.azimuth?.value  ?: 0f
                            onDistanceUpdate(addr.toString(), dist, az)
                        }
                        is RangingResult.RangingResultPeerDisconnected -> {
                            onError("Peer disconnected: ${result.device.address}")
                        }
                        is RangingResult.RangingResultInitialized -> {
                            Log.d(TAG, "Ranging initialized for ${result.device.address}")
                        }
                        else -> {
                            // 기타 결과 무시
                        }
                    }
                }
            }
            _isRanging = true

        } catch (e: Exception) {
            val errorMsg = "레인징 시작 오류: ${e.message}"
            Log.e(TAG, errorMsg)
            onError(errorMsg)
            _isRanging = false
        }
    }

    // 레인징 중지
    fun stopRanging() {
        rangingJob?.cancel()
        rangingJob = null
        uwbSessionScope = null
        _isRanging = false
    }

    // 기존 정보 초기화 (예: 태그 재스캔 전)
    fun clearTagInfos() {
        stopRanging()
        tagInfos.clear()
    }
}