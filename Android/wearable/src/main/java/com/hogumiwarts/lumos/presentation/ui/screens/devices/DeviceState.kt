package com.hogumiwarts.lumos.presentation.ui.screens.devices

import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.DeviceListData
import com.hogumiwarts.lumos.domain.model.GetDevicesResult

sealed class DeviceState {
    object Idle : DeviceState()
    object Loading : DeviceState()
    data class Loaded(val data: List<DeviceListData>) : DeviceState()
    data class Error(val error: CommonError) : DeviceState()
}