package com.hogumiwarts.lumos.ui.screens.control.light

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.light.LightBrightData
import com.hogumiwarts.domain.model.light.LightTemperatureData

sealed class LightTemperatureState {
    object Idle : LightTemperatureState()
    object Loading : LightTemperatureState()
    data class Loaded(val data: LightTemperatureData) : LightTemperatureState()
    data class Error(val error: CommonError) : LightTemperatureState()
}