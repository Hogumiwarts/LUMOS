package com.hogumiwarts.lumos.utils

import android.content.Context
import android.util.Log
import androidx.core.uwb.RangingParameters
import androidx.core.uwb.RangingResult
import androidx.core.uwb.UwbAddress
import androidx.core.uwb.UwbComplexChannel
import androidx.core.uwb.UwbControllerSessionScope
import androidx.core.uwb.UwbDevice
import androidx.core.uwb.UwbManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.atan2

class UwbRangingManager(private val context: Context) {
    private val TAG = "UwbRangingManager"
    private lateinit var uwbManager: UwbManager
    private var uwbSessionScope: UwbControllerSessionScope? = null
    private var rangingJob: Job? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private var _isRanging = false
    val isRanging: Boolean get() = _isRanging

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

    // SmartTag2의 UWB 주소와 채널 정보 (실제로는 SmartThings API에서 가져와야 함)
    private fun getSmartTagInfo(): Pair<String, UwbComplexChannel> {
        // 예시 값 - 실제로는 SmartThings API에서 가져와야 함
        // UwbAddress는 String 형태로 전달 (MAC 주소 형식)
        return Pair(
            "01:02:03:04:05:06", // String 형식의 주소
            UwbComplexChannel(
                channel = 9,
                preambleIndex = 10
            )
        )
    }

    // 레인징 시작
    suspend fun startRanging(
        onDistanceUpdate: (Float, Float) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            // 이미 레인징 중이면 중지
            if (_isRanging) {
                stopRanging()
            }

            // UWB 컨트롤러 세션 스코프 가져오기
            uwbSessionScope = uwbManager.controllerSessionScope()

            // 스마트태그 정보 가져오기
            val tagInfo = getSmartTagInfo()

            // UWB 디바이스 생성
            val peerDevice = UwbDevice.createForAddress(tagInfo.first)

            // 레인징 파라미터 설정
            val rangingParameters = RangingParameters(
                uwbConfigType = 1,
                sessionId = 0,  // 세션 ID
                subSessionId = 0,  // 서브 세션 ID
                sessionKeyInfo = null,  // 세션 암호화 키 (불필요시 null)
                subSessionKeyInfo = null,  // 서브 세션 암호화 키 (불필요시 null)
                complexChannel = tagInfo.second,  // UWB 채널 정보
                peerDevices = listOf(peerDevice),  // 타겟 디바이스 목록
                updateRateType = RangingParameters.RANGING_UPDATE_RATE_AUTOMATIC,  // 업데이트 속도
                uwbRangeDataNtfConfig = null,  // 통지 설정 (기본값 사용)
                slotDurationMillis = RangingParameters.RANGING_SLOT_DURATION_2_MILLIS,  // 슬롯 지속 시간
                isAoaDisabled = false  // AoA(Angle of Arrival) 비활성화 여부
            )


            // 세션 준비 및 레인징 결과 Flow 수집
            rangingJob = coroutineScope.launch {
                uwbSessionScope?.let { scope ->
                    scope.prepareSession(rangingParameters).collect { result ->
                        when (result) {
                            is RangingResult.RangingResultPosition -> {
                                try {
                                    // position 객체의 구조 로깅
                                    Log.d(TAG, "Position: ${result.position}")

                                    // RangingMeasurement 객체를 Float로 변환
                                    // 거리 정보가 있는 경우
                                    val distanceObj = result.position.distance
                                    val distance = distanceObj?.value ?: 0f

                                    // 각도 계산
                                    // position에서 azimuth(방위각) 값을 직접 사용
                                    val azimuth = result.position.azimuth?.value ?: 0f

                                    // 콜백으로 값 전달
                                    onDistanceUpdate(distance, azimuth)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error processing position data: ${e.message}")
                                    e.printStackTrace()
                                    onError("데이터 처리 오류: ${e.message}")
                                }
                            }
                            is RangingResult.RangingResultPeerDisconnected -> {
                                // 피어 연결 해제 처리
                                val errorMsg = "피어 연결 해제: ${result.device.address}"
                                Log.e(TAG, errorMsg)
                                onError(errorMsg)
                            }
                            is RangingResult.RangingResultInitialized -> {
                                // 초기화 완료 처리
                                Log.d(TAG, "레인징 초기화 완료: ${result.device.address}")
                            }
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
}