package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchIntent

sealed class LightIntent {
    data class LoadLightStatus(val deviceId: Long): LightIntent()

    data class ChangeLightPower(val deviceId: Long, val activated: Boolean): LightIntent()

    data class ChangeLightBright(val deviceId: Long, val brightness: Int): LightIntent()

    data class ChangeLightColor(val deviceId: Long, val color: Int): LightIntent()

}