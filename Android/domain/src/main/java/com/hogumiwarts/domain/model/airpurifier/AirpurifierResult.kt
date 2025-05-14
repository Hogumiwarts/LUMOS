package com.hogumiwarts.domain.model.airpurifier

import com.hogumiwarts.domain.model.CommonError

sealed class AirpurifierResult{
    data class Success(
        val data : AirpurifierData
    ):AirpurifierResult()

    data class Error(val error: CommonError) : AirpurifierResult()
}


