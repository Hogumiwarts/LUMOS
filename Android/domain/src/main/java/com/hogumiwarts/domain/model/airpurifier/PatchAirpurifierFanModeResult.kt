package com.hogumiwarts.domain.model.airpurifier

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.ControlData

sealed class PatchAirpurifierFanModeResult {
    data class Success(
        val data : ControlData
    ):PatchAirpurifierFanModeResult()

    data class Error(val error: CommonError) : PatchAirpurifierFanModeResult()
}