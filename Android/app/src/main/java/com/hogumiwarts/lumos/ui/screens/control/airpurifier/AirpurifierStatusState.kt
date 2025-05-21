package com.hogumiwarts.lumos.ui.screens.control.airpurifier

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.airpurifier.AirpurifierData

sealed class AirpurifierStatusState {
    object Idle : AirpurifierStatusState()
    object Loading : AirpurifierStatusState()
    data class Loaded(val data: AirpurifierData) : AirpurifierStatusState()
    data class Error(val error: CommonError) : AirpurifierStatusState()
}