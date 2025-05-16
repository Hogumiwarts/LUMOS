package com.hogumiwarts.lumos.ui.screens.control.airpurifier

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData
import com.hogumiwarts.domain.model.airpurifier.AirpurifierData

sealed class AirpurifierPowerState {
    object Idle : AirpurifierPowerState()
    object Loading : AirpurifierPowerState()
    data class Loaded(val data: ControlData) : AirpurifierPowerState()
    data class Error(val error: CommonError) : AirpurifierPowerState()
}