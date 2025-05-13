package com.hogumiwarts.lumos.domain.model

sealed class PatchSwitchPowerResult{
    data class Success(
        val data : SwitchPowerData
    ):PatchSwitchPowerResult()

    data class Error(val error: CommonError) : PatchSwitchPowerResult()
}