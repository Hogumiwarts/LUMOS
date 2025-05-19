package com.hogumiwarts.domain.model.gesture

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.audio.AudioPowerData

sealed class GestureResult {
    data class Success(
        val data : AudioPowerData
    ): GestureResult()

    data class Error(val error: CommonError) : GestureResult()
}