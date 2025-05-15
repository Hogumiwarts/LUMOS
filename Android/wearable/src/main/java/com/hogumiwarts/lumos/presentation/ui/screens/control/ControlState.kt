package com.hogumiwarts.lumos.presentation.ui.screens.control

import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.ControlData

sealed class ControlState {
    object Idle : ControlState()
    object Loading : ControlState()
    data class Loaded(val data: ControlData) : ControlState()
    data class Error(val error: CommonError) : ControlState()
}