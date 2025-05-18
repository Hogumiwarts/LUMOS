package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.light.LightBrightData
import com.hogumiwarts.domain.model.light.LightStatusData

sealed class LightBrightState {
    object Idle : LightBrightState()
    object Loading : LightBrightState()
    data class Loaded(val data: LightBrightData) : LightBrightState()
    data class Error(val error: CommonError) : LightBrightState()
}