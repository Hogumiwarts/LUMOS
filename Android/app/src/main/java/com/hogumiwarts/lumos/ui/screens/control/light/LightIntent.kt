package com.hogumiwarts.lumos.ui.screens.control.light


sealed class LightIntent {
    data class LoadLightStatus(val deviceId: Int): LightIntent()

    data class ChangeLightPower(val deviceId: Int, val activated: Boolean): LightIntent()

    data class ChangeLightBright(val deviceId: Long, val brightness: Int): LightIntent()

    data class ChangeLightColor(val deviceId: Long, val color: Float, val saturation:Float=100f): LightIntent()

    data class ChangeLightTemperature(val deviceId: Long, val temperature: Int): LightIntent()

}