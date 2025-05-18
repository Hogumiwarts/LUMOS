package com.hogumiwarts.lumos.ui.screens.control.airpurifier


sealed class AirpurifierIntent {
    data class LoadAirpurifierStatus(val deviceId: Long): AirpurifierIntent()

    data class ChangeAirpurifierPower(val deviceId: Long, val activated: Boolean): AirpurifierIntent()

    data class ChangeAirpurifierFenMode(val deviceId: Long, val fanMode: String): AirpurifierIntent()
}