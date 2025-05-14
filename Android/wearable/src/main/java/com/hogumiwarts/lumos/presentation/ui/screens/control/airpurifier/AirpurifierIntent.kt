package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightIntent

sealed class AirpurifierIntent {
    data class LoadAirpurifierStatus(val deviceId: Long): AirpurifierIntent()
}