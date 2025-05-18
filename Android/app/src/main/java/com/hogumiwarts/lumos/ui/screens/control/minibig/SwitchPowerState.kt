package com.hogumiwarts.lumos.ui.screens.control.minibig

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData

sealed class SwitchPowerState {
    object Idle : SwitchPowerState()
    object Loading : SwitchPowerState()
    data class Loaded(val data: ControlData) : SwitchPowerState()
    data class Error(val error: CommonError) : SwitchPowerState()
}