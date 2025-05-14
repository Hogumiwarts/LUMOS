package com.hogumiwarts.lumos.ui.screens.control

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun getDevicePosition(address: String) = uwbRanging.getDevicePosition(address)

    fun prepareSession() {
        uwbRanging.prepareSession()
    }

    fun startMultiRanging(): Boolean {
        return uwbRanging.startMultiRanging()
    }

//    fun startRanging(remoteAdr: String): Boolean {
//        // 이미 활성화된 경우 중복 호출 방지
//        if (rangingActive) return true
//        return uwbRanging.startRanging(remoteAdr)
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
        // ViewModel이 소멸될 때 레인징을 중지
        if (rangingActive) {
            uwbRanging.stopRanging()
        }
    }
}