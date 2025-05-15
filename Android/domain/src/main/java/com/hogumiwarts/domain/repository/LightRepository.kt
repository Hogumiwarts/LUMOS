package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.light.GetLightStatusResult
import com.hogumiwarts.domain.model.light.LightBrightResult
import com.hogumiwarts.domain.model.light.LightColorResult
import com.hogumiwarts.domain.model.light.LightTemperatureResult


interface LightRepository {

    suspend fun getLightStatus(deviceId: Long): GetLightStatusResult

    suspend fun patchLightPower(deviceId: Long, activated: Boolean): PatchSwitchPowerResult

    suspend fun patchLightBright(deviceId: Long, brightness: Int): LightBrightResult

    suspend fun patchLightColor(deviceId: Long, color: Int,saturation: Float): LightColorResult

    suspend fun patchLightTemperature(deviceId: Long, temperature: Int): LightTemperatureResult
}