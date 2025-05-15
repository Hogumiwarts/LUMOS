package com.hogumiwarts.domain.model.airpurifier

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData

sealed class PatchAirpurifierPowerResult {
    data class Success(
        val data : ControlData
    ):PatchAirpurifierPowerResult()

    data class Error(val error: CommonError) : PatchAirpurifierPowerResult()
}