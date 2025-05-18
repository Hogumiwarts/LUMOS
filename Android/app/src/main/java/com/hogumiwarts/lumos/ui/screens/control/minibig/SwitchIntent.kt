package com.hogumiwarts.lumos.ui.screens.control.minibig

sealed class SwitchIntent {
    data class LoadSwitchStatus(val deviceId: Long): SwitchIntent()
    data class ChangeSwitchPower(val deviceId: Long, val activated: Boolean ): SwitchIntent()
}