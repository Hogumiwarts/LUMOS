package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.light.LightStatusData


sealed class LightStatusState {
    object Idle : LightStatusState()
    object Loading : LightStatusState()
    data class Loaded(val data: LightStatusData) : LightStatusState()
    data class Error(val error: CommonError) : LightStatusState()
}