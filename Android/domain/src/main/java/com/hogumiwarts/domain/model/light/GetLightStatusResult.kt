package com.hogumiwarts.domain.model.light

import com.hogumiwarts.domain.model.CommonError

sealed class GetLightStatusResult{
    data class Success(
        val data : LightStatusData
    ):GetLightStatusResult()

    data class Error(val error: CommonError) : GetLightStatusResult()
}

