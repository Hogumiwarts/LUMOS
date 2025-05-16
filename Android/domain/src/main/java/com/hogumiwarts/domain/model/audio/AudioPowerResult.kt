package com.hogumiwarts.domain.model.audio

import com.hogumiwarts.domain.model.CommonError

sealed class AudioPowerResult {
    data class Success(
        val data : AudioPowerData
    ): AudioPowerResult()

    data class Error(val error: CommonError) : AudioPowerResult()
}