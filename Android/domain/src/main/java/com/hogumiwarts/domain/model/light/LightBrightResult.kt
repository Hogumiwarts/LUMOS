package com.hogumiwarts.domain.model.light

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData
import com.hogumiwarts.domain.model.PatchSwitchPowerResult

sealed class LightBrightResult {
    data class Success(
        val data: LightBrightData
    ) : LightBrightResult()

    data class Error(val error: CommonError) : LightBrightResult()
}