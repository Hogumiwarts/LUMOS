package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.minibig.GetSwitchStatusResult


interface SwitchRepository {

    suspend fun getSwitchStatus(deviceId: Int): GetSwitchStatusResult

    suspend fun patchSwitchPower(deviceId: Int, activated: Boolean): PatchSwitchPowerResult

}