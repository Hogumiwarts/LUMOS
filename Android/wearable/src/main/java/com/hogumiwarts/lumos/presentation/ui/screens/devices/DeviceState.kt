package com.hogumiwarts.lumos.presentation.ui.screens.devices

sealed class DeviceState {
    object Idle : DeviceState()
    object Loading : DeviceState()
    data class Loaded(val data: List<com.hogumiwarts.domain.model.devices.DeviceListData>) : DeviceState()
    data class Error(val error: com.hogumiwarts.domain.model.CommonError) : DeviceState()
}