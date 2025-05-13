package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.SwitchStatusData
import com.hogumiwarts.lumos.domain.model.light.LightStatusData
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchStatusState

sealed class LightStatusState {
    object Idle : LightStatusState()
    object Loading : LightStatusState()
    data class Loaded(val data: LightStatusData) : LightStatusState()
    data class Error(val error: CommonError) : LightStatusState()
}