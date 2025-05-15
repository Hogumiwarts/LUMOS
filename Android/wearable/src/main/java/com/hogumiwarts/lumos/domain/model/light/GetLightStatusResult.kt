package com.hogumiwarts.lumos.domain.model.light

import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.GetSwitchStatusResult
import com.hogumiwarts.lumos.domain.model.SwitchStatusData

sealed class GetLightStatusResult{
    data class Success(
        val data : LightStatusData
    ):GetLightStatusResult()

    data class Error(val error: CommonError) : GetLightStatusResult()
}

