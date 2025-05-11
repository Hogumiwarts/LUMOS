package com.hogumiwarts.lumos.ui.screens.Routine.routineDeviceList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hogumiwarts.lumos.ui.common.MyDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RoutineDeviceListViewModel @Inject constructor() : ViewModel() {
    val selectedDeviceId = mutableStateOf<Int?>(null)
    val showDialog = mutableStateOf(false)

    fun onDeviceClicked(device: MyDevice) {
        if (!device.isActive) {
            showDialog.value = true
        } else {
            selectedDeviceId.value =
                if (selectedDeviceId.value == device.deviceId) null else device.deviceId
        }
    }

    fun dismissDialog() {
        showDialog.value = false
    }

    fun getSelectedDevice(devices: List<MyDevice>): MyDevice? {
        return devices.find { it.deviceId == selectedDeviceId.value }
    }
}