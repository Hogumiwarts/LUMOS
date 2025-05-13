package com.hogumiwarts.lumos.domain.usecase

import com.hogumiwarts.lumos.domain.model.GetSwitchStatusResult
import com.hogumiwarts.lumos.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.lumos.domain.model.light.GetLightStatusResult
import com.hogumiwarts.lumos.domain.repository.LightRepository
import com.hogumiwarts.lumos.domain.repository.SwitchRepository
import javax.inject.Inject

class LightUseCase @Inject constructor(
    private val lightRepository: LightRepository
) {
    suspend fun getLightStatus(deviceId: Long): GetLightStatusResult {
        val data = lightRepository.getLightStatus(deviceId)
        return data
    }

    suspend fun patchLightPower(deviceId: Long, activated: Boolean): PatchSwitchPowerResult {
        val data = lightRepository.patchLightPower(deviceId =deviceId,activated = activated)
        return data
    }

    suspend fun patchLightBright(deviceId: Long, brightness: Int): PatchSwitchPowerResult {
        val data = lightRepository.patchLightBright(deviceId =deviceId,brightness = brightness)
        return data
    }

    suspend fun patchLightColor(deviceId: Long, color: String): PatchSwitchPowerResult {
        val data = lightRepository.patchLightColor(deviceId =deviceId,color = color)
        return data
    }






}