package com.hogumiwarts.lumos.presentation.ui.screens.control.minibig

import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceIntent

sealed class SwitchStatusIntent {
    data class LoadSwitchStatus(val deviceId: Long): SwitchStatusIntent()
}