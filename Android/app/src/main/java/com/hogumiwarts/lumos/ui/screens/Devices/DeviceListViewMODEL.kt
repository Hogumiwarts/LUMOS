package com.hogumiwarts.lumos.ui.screens.Devices

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hogumiwarts.lumos.ui.common.MyDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class DeviceListViewModel @Inject constructor() : ViewModel() {
    val selectedDeviceId = mutableStateOf<Int?>(null)
    val showDialog = mutableStateOf(false)

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
