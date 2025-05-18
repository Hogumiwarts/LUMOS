package com.hogumiwarts.lumos.ui.screens.control.airpurifier


sealed class AirpurifierIntent {
    data class LoadAirpurifierStatus(val deviceId: Int): AirpurifierIntent()

    data class ChangeAirpurifierPower(val deviceId: Int, val activated: Boolean): AirpurifierIntent()

    data class ChangeAirpurifierFenMode(val deviceId: Int, val fanMode: String): AirpurifierIntent()
}