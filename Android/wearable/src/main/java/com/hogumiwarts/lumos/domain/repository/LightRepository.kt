package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.lumos.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.lumos.domain.model.light.GetLightStatusResult

interface LightRepository {

    suspend fun getLightStatus(deviceId: Long): GetLightStatusResult

    suspend fun patchLightPower(deviceId: Long, activated: Boolean): PatchSwitchPowerResult

    suspend fun patchLightBright(deviceId: Long, brightness: Int): PatchSwitchPowerResult

    suspend fun patchLightColor(deviceId: Long, color: Int): PatchSwitchPowerResult
}