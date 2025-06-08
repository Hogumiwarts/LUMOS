package com.hogumiwarts.domain.model.minibig

import com.hogumiwarts.domain.model.CommonError

sealed class GetSwitchStatusResult {
    data class Success(
        val data : SwitchStatusData
    ):GetSwitchStatusResult()

    data class Error(val error: CommonError) : GetSwitchStatusResult()
}