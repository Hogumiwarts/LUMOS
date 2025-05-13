package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchIntent

sealed class LightIntent {
    data class LoadLightStatus(val deviceId: Long): LightIntent()
}