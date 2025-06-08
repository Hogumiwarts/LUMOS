package com.hogumiwarts.lumos.presentation.ui.screens.control

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData
import com.hogumiwarts.domain.model.light.LightBrightData


sealed class ControlState {
    object Idle : ControlState()
    object Loading : ControlState()
    data class Loaded(val data: ControlData) : ControlState()
    data class Error(val error: CommonError) : ControlState()
}