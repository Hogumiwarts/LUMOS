package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.light.GetLightStatusResult
import com.hogumiwarts.domain.model.light.LightBrightResult
import com.hogumiwarts.domain.model.light.LightColorResult
import com.hogumiwarts.domain.model.light.LightTemperatureResult
import com.hogumiwarts.domain.repository.LightRepository
import javax.inject.Inject

class LightUseCase @Inject constructor(
    private val lightRepository: LightRepository
) {
    suspend fun getLightStatus(deviceId: Int): GetLightStatusResult {
        val data = lightRepository.getLightStatus(deviceId)
        return data
    }

    suspend fun patchLightPower(deviceId: Int, activated: Boolean): PatchSwitchPowerResult {
        val data = lightRepository.patchLightPower(deviceId =deviceId, activated = activated)
        return data
    }

    suspend fun patchLightBright(deviceId: Long, brightness: Int): LightBrightResult {
        val data = lightRepository.patchLightBright(deviceId =deviceId,brightness = brightness)
        return data
    }

    suspend fun patchLightColor(deviceId: Long, color: Float,saturation:Float): LightColorResult {
        val data = lightRepository.patchLightColor(deviceId =deviceId,color = color,saturation)
        return data
    }

    suspend fun patchLightTemperature(deviceId: Long, temperature: Int): LightTemperatureResult {
        val data = lightRepository.patchLightTemperature(deviceId =deviceId,temperature = temperature)
        return data
    }






}