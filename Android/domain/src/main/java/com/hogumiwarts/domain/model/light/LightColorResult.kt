package com.hogumiwarts.domain.model.light

import com.hogumiwarts.domain.model.CommonError

sealed class LightColorResult {
    data class Success(
        val data: LightColorData
    ) : LightColorResult()

    data class Error(val error: CommonError) : LightColorResult()
}