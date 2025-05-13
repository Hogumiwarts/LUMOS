package com.hogumiwarts.lumos.presentation.ui.screens.control.minibig

import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.DeviceListData
import com.hogumiwarts.lumos.domain.model.SwitchStatusData
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceState

sealed class SwitchStatusState {
    object Idle : SwitchStatusState()
    object Loading : SwitchStatusState()
    data class Loaded(val data: SwitchStatusData) : SwitchStatusState()
    data class Error(val error: CommonError) : SwitchStatusState()
}