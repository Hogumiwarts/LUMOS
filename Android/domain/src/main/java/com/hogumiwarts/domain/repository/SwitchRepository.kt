package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.minibig.GetSwitchStatusResult


interface SwitchRepository {

    suspend fun getSwitchStatus(deviceId: Long): GetSwitchStatusResult

    suspend fun patchSwitchPower(deviceId: Long, activated: Boolean): PatchSwitchPowerResult

}