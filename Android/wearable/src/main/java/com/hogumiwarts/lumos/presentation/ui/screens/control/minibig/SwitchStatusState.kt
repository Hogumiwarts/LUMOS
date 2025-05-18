package com.hogumiwarts.lumos.presentation.ui.screens.control.minibig

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.minibig.SwitchStatusData

sealed class SwitchStatusState {
    object Idle : SwitchStatusState()
    object Loading : SwitchStatusState()
    data class Loaded(val data: SwitchStatusData) : SwitchStatusState()
    data class Error(val error: CommonError) : SwitchStatusState()
}