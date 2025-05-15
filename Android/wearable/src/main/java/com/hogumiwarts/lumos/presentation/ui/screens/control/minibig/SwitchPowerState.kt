package com.hogumiwarts.lumos.presentation.ui.screens.control.minibig

import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.SwitchPowerData

sealed class SwitchPowerState {
    object Idle : SwitchPowerState()
    object Loading : SwitchPowerState()
    data class Loaded(val data: SwitchPowerData) : SwitchPowerState()
    data class Error(val error: CommonError) : SwitchPowerState()
}