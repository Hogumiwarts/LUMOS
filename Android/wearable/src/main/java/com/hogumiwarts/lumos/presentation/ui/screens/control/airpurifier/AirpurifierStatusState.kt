package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.airpurifier.AirpurifierData
import com.hogumiwarts.lumos.domain.model.light.LightStatusData
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightStatusState

sealed class AirpurifierStatusState {
    object Idle : AirpurifierStatusState()
    object Loading : AirpurifierStatusState()
    data class Loaded(val data: AirpurifierData) : AirpurifierStatusState()
    data class Error(val error: CommonError) : AirpurifierStatusState()
}