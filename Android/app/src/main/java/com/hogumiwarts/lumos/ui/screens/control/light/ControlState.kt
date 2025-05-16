package com.hogumiwarts.lumos.ui.screens.control.light

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData


sealed class ControlState {
    object Idle : ControlState()
    object Loading : ControlState()
    data class Loaded(val data: ControlData) : ControlState()
    data class Error(val error: CommonError) : ControlState()
}