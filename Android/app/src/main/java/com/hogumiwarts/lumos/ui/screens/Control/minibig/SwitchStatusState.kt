package com.hogumiwarts.lumos.ui.screens.Control.minibig

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.light.LightStatusData
import com.hogumiwarts.domain.model.minibig.SwitchStatusData
import com.hogumiwarts.lumos.ui.screens.control.light.LightStatusState

sealed class SwitchStatusState {
    object Idle : SwitchStatusState()
    object Loading : SwitchStatusState()
    data class Loaded(val data: SwitchStatusData) : SwitchStatusState()
    data class Error(val error: CommonError) : SwitchStatusState()
}