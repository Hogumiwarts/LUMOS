package com.hogumiwarts.lumos.ui.screens.devices

sealed class DeviceIntent {
    data class LoadAudioPower(val deviceId: Long, val activated: Boolean): DeviceIntent()
    data class LoadLightPower(val deviceId: Long, val activated: Boolean): DeviceIntent()
    data class LoadSwitchPower(val deviceId: Long, val activated: Boolean): DeviceIntent()
    data class LoadAirpurufuerPower(val deviceId: Long, val activated: Boolean): DeviceIntent()
}