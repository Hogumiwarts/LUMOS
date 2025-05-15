package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.lumos.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.lumos.domain.model.GetSwitchStatusResult

interface SwitchRepository {

    suspend fun getSwitchStatus(deviceId: Long): GetSwitchStatusResult

    suspend fun patchSwitchPower(deviceId: Long, activated: Boolean): PatchSwitchPowerResult

}