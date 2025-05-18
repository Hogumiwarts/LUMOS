package com.hogumiwarts.lumos.ui.screens.control

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(
    private val uwbRanging: UwbRanging
) : ViewModel() {

    // UwbRanging의 상태를 노출
    val localAddress get() = uwbRanging.localAdr
    val rangingActive get() = uwbRanging.rangingActive
    val rangingPosition get() = uwbRanging.rangingPosition

    // 멀티 디바이스 관련 속성
    val rangingPositions get() = uwbRanging.rangingPositions
    val controleeAddresses get() = uwbRanging.getControleeAddresses()
    val connectedDevices get() = uwbRanging.getConnectedDevices()

    // 세션 준비
    val sessionReady get() = uwbRanging.sessionReady


    /** 내부 감시 Job */
    private var detectionJob: Job? = null
    // 탐지 상태
    var isDetecting by mutableStateOf(false)
        private set
    /** ★ 탐지 결과(기기 이름). null 이면 미탐지 / 탐지 중 */
    var detectedDeviceName by mutableStateOf<String?>(null)
        private set


    /** ★ 10 초 타임아웃 + 3 초 연속 구간 유지 로직 */
    fun startDetection() {
        // 이미 돌고 있던 감시 Job이 있으면 취소
        detectionJob?.cancel()
        detectedDeviceName = null
        isDetecting = true

        val targetAdr = uwbRanging.getControleeAddresses().first()

        detectionJob = viewModelScope.launch {
            val overallDeadline = System.currentTimeMillis() + 10_000L // 10초 타임아웃
            var currentRange: String? = null     // 현재 머무는 구간 이름
            var rangeStart = 0L                  // 해당 구간 머문 시각

            while (System.currentTimeMillis() < overallDeadline && isActive) {
                // 방위각
                val azimuth = uwbRanging.getDevicePosition(targetAdr)?.azimuth?.value ?: 180F

                val range = when (azimuth) {
                    in -90f..-20f -> "공기청정기"
                    in -15f..15f -> "조명"
                    in 20f..90f -> "스피커"
                    else -> null
                }

                if (range != null) {
                    if (range == currentRange) {
                        // 같은 구간에서 누적 시간 확인
                        if (System.currentTimeMillis() - rangeStart >= 3_000L) {
                            detectedDeviceName = range      // ✅ 성공
                            break
                        }
                    } else {
                        // 새 구간 진입 → 타이머 리셋
                        currentRange = range
                        rangeStart = System.currentTimeMillis()
                    }
                } else {
                    // 구간을 벗어남 → 리셋
                    currentRange = null
                }
                delay(100) // 샘플 주기 10 Hz
            }
        }
    }

    fun cancelDetection() {
        detectionJob?.cancel()
        isDetecting = false
    }


    fun getDevicePosition(address: String) = uwbRanging.getDevicePosition(address)

    fun prepareSession() {
        uwbRanging.prepareSession()
    }

    fun startSingleRanging(): Boolean {
        return uwbRanging.startSingleRanging()
    }

//    fun startMultiRanging(): Boolean {
//        return uwbRanging.startMultiDeviceRanging()
//    }
//
//    fun startRanging(): Boolean {
//        // 이미 활성화된 경우 중복 호출 방지
//        if (rangingActive) return true
//        return uwbRanging.startRanging()
//    }

    fun stopRanging() {
        uwbRanging.stopRanging()
    }

    fun cleanupSession() {
        uwbRanging.cleanupSession()
    }

    fun resetAddress() {
        uwbRanging.resetAddress()
    }

    fun resetSession() {
        uwbRanging.cleanupSession()
        uwbRanging.resetAddress()
        uwbRanging.prepareSession()
    }

    override fun onCleared() {
        super.onCleared()
        detectionJob?.cancel()
//        // ViewModel이 소멸될 때 레인징을 중지
//        if (rangingActive) {
//            uwbRanging.stopRanging()
//        }
    }
}