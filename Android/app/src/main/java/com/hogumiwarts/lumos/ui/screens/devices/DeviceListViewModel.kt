package com.hogumiwarts.lumos.ui.screens.devices

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hogumiwarts.lumos.ui.common.MyDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class DeviceListViewModel @Inject constructor() : ViewModel() {
    val selectedDeviceId = mutableStateOf<Int?>(null)
    val showDialog = mutableStateOf(false)

    private val _isLinked = MutableStateFlow(false) // SmartThings 계정 연동 여부
    val isLinked: StateFlow<Boolean> = _isLinked

    fun checkAccountLinked() {
        //todo: 실제 SmartThings 연동 여부 확인 로직
        _isLinked.value = linkCheckLogic()
    }

    // 임시
    private fun linkCheckLogic(): Boolean {
        //todo: 임시로 연동되어 있지 않다고 가정 -> 테스트 시 아래 boolean 값 바꾸면 됩니당
        return false
    }

    fun onDeviceClicked(device: MyDevice) {
        if (!device.isActive) {
            showDialog.value = true
        } else {
            //todo: 각 기기의 제어 화면으로 이동
        }
    }

    fun dismissDialog() {
        showDialog.value = false
    }

    fun getSelectedDevice(devices: List<MyDevice>): MyDevice? {
        return devices.find { it.deviceId == selectedDeviceId.value }
    }
}
