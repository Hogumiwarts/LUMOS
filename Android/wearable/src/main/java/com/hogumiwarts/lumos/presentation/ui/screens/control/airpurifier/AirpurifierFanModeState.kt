package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData

sealed class AirpurifierFanModeState {
    object Idle : AirpurifierFanModeState()
    object Loading : AirpurifierFanModeState()
    data class Loaded(val data: ControlData) : AirpurifierFanModeState()
    data class Error(val error: CommonError) : AirpurifierFanModeState()
}

