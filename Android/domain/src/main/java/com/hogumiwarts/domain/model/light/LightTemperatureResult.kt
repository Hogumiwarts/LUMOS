package com.hogumiwarts.domain.model.light

import com.hogumiwarts.domain.model.CommonError

sealed class LightTemperatureResult {
    data class Success(
        val data: LightTemperatureData
    ) : LightTemperatureResult()

    data class Error(val error: CommonError) : LightTemperatureResult()
}