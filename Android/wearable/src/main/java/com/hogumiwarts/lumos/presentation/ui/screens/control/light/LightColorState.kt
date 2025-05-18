package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.light.LightBrightData
import com.hogumiwarts.domain.model.light.LightColorData

sealed class LightColorState {
    object Idle : LightColorState()
    object Loading : LightColorState()
    data class Loaded(val data: LightColorData) : LightColorState()
    data class Error(val error: CommonError) : LightColorState()
}