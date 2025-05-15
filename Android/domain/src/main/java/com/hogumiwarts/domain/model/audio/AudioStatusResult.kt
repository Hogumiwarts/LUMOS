package com.hogumiwarts.domain.model.audio

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.airpurifier.AirpurifierData
import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult

sealed class AudioStatusResult{
    data class Success(
        val data : AudioStatusData
    ): AudioStatusResult()

    data class Error(val error: CommonError) : AudioStatusResult()
}
