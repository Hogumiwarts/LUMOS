package com.hogumiwarts.lumos.domain.model

sealed class GetSwitchStatusResult {
    data class Success(
        val data : SwitchStatusData
    ):GetSwitchStatusResult()

    data class Error(val error: CommonError) : GetSwitchStatusResult()
}