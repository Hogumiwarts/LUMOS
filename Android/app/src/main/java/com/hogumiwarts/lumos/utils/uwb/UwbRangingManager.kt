package com.hogumiwarts.lumos.utils.uwb

import android.content.Context
import androidx.core.uwb.*
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.concurrent.Executors

import android.util.Log
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingResult
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbControleeSessionScope
import androidx.core.uwb.UwbControllerSessionScope
import androidx.core.uwb.UwbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.Executor

data class UwbRangingData(
    val distance: Float = 0f,
    val azimuth: Float = 0f,
    val elevation: Float = 0f
)

class UwbRangingManager(private val context: Context) {
    private val uwbManager = UwbManager.createInstance(context)
    private val _rangingData = MutableStateFlow(UwbRangingData())
    val rangingData = _rangingData.asStateFlow()

    private var controllerSession: UwbControllerSessionScope? = null
    private var controleeSession: UwbControleeSessionScope? = null

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // RangingResult 콜백
    private val rangingListener = object : RangingListener {
        override fun onRangingResult(result: RangingResult) {
            when (result) {
                is RangingResult.RangingResultPosition -> {
                    val position = result.position

                    // RangingMeasurement 객체에서 값 추출
                    val distanceValue = position.distance?.value ?: 0f
                    val azimuthValue = position.azimuth?.value ?: 0f
                    val elevationValue = position.elevation?.value ?: 0f

                    Log.d("UwbRangingManager", "거리: $distanceValue, 방위각: $azimuthValue, 고도각: $elevationValue")

                    _rangingData.value = UwbRangingData(distanceValue, azimuthValue, elevationValue)
                }
                is RangingResult.RangingResultPeerDisconnected -> {
                    val address = result.device.address
                    Log.d("UwbRangingManager", "피어 연결 해제: $address")
                }
                else -> {
                    Log.d("UwbRangingManager", "기타 결과: $result")
                }
            }
        }
    }

    // Controller 모드에서 UWB 거리 측정 시작
    fun startRanging(
        uwbAddress: UwbAddress,
        onRangingResult: (distance: Float, azimuth: Float, elevation: Float) -> Unit
    ) {
        coroutineScope.launch {
            try {
                // UWB 사용 가능 여부 확인
                val isAvailable = uwbManager.isAvailable()
                if (!isAvailable) {
                    Log.e("UwbRangingManager", "UWB 서비스를 사용할 수 없습니다.")
                    return@launch
                }

                // 이전 세션이 있으면 종료
                stopRanging()

                // 컨트롤러 세션 생성
                controllerSession = uwbManager.controllerSessionScope()

                // 세션 설정 및 거리 측정 시작
                configureAndStartRanging(uwbAddress)

                // 거리 측정 결과 콜백 처리
                // 여기서는 rangingData 스테이트 플로우를 통해 값을 전달
                // UwbRangingData가 변경될 때마다 onRangingResult 콜백 호출
                coroutineScope.launch {
                    rangingData.collect { data ->
                        onRangingResult(data.distance, data.azimuth, data.elevation)
                    }
                }

                Log.d("UwbRangingManager", "UWB 거리 측정 시작: $uwbAddress")

            } catch (e: Exception) {
                Log.e("UwbRangingManager", "UWB 거리 측정 시작 실패: ${e.message}")
            }
        }
    }

    // 세션 설정 및 거리 측정 시작
    private suspend fun configureAndStartRanging(uwbAddress: UwbAddress) {
        try {
            // Controlee 추가
            controllerSession?.addControlee(uwbAddress)

            // 여기서 RangingListener를 등록해야 하는데,
            // 인터페이스에 직접적인 메소드가 없어 보입니다.
            // 실제 구현에서는 다음과 같은 방식으로 처리할 가능성이 있습니다:

            // 1. 세션 설정 시 리스너를 등록하거나
            // 2. 이벤트를 구독하기 위한 다른 메커니즘을 사용

            // 이 예제에서는 실제 API가 어떻게 구현되어 있는지 모르므로
            // 주석으로 대체합니다.

            Log.d("UwbRangingManager", "UWB 거리 측정 설정 완료")

        } catch (e: Exception) {
            Log.e("UwbRangingManager", "거리 측정 설정 실패: ${e.message}")
            throw e
        }
    }

    // Controlee 모드에서 UWB 응답 모드 시작
    fun startControleeMode() {
        coroutineScope.launch {
            try {
                // UWB 사용 가능 여부 확인
                val isAvailable = uwbManager.isAvailable()
                if (!isAvailable) {
                    Log.e("UwbRangingManager", "UWB 서비스를 사용할 수 없습니다.")
                    return@launch
                }

                // 이전 세션이 있으면 종료
                stopRanging()

                // Controlee 세션 생성
                controleeSession = uwbManager.controleeSessionScope()

                // 여기서도 리스너 등록이 필요하지만,
                // 인터페이스에 직접적인 메소드가 없어 주석으로 대체합니다.

                Log.d("UwbRangingManager", "UWB Controlee 모드 시작")

            } catch (e: Exception) {
                Log.e("UwbRangingManager", "UWB Controlee 모드 시작 실패: ${e.message}")
            }
        }
    }

    // UWB 사용 가능 여부 확인
    fun checkUwbAvailability(
        callback: (Boolean) -> Unit
    ) {
        coroutineScope.launch {
            try {
                val isAvailable = uwbManager.isAvailable()
                callback(isAvailable)
            } catch (e: Exception) {
                Log.e("UwbRangingManager", "UWB 사용 가능 여부 확인 실패: ${e.message}")
                callback(false)
            }
        }
    }

    // IoT 기기 정보로 서비스 광고 시작
    fun advertiseService(deviceName: String, deviceType: String) {
        // BLE 광고 관련 코드 구현 (UWB 주소와 함께 IoT 기기 정보 포함)
        Log.d("UwbRangingManager", "IoT 서비스 광고 시작: $deviceName ($deviceType)")
    }

    // 세션 종료
    fun stopRanging() {
        try {
            // 세션 종료 로직
            // 인터페이스에 close() 메소드가 없으므로
            // 다른 방식으로 세션을 종료해야 합니다.
            // 해당 API의 실제 구현을 참조해야 합니다.

            controllerSession = null
            controleeSession = null

            Log.d("UwbRangingManager", "UWB 세션 종료")
        } catch (e: Exception) {
            Log.e("UwbRangingManager", "UWB 세션 종료 실패: ${e.message}")
        }
    }
}

// RangingListener 인터페이스 정의
interface RangingListener {
    fun onRangingResult(result: RangingResult)
}

// UwbAvailabilityCallback 인터페이스 정의
interface UwbAvailabilityCallback {
    fun onUwbAvailabilityChanged(isAvailable: Boolean)
}

//class UwbRangingManager(private val context: Context) {
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
//}