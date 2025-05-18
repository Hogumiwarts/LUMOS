package com.hogumiwarts.lumos.ui.screens.Control.minibig

import com.hogumiwarts.lumos.ui.screens.control.light.LightIntent

sealed class SwitchIntent {
    data class LoadSwitchStatus(val deviceId: Long): SwitchIntent()
}