package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierFanModeResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult
import com.hogumiwarts.domain.repository.AirpurifierRepository
import javax.inject.Inject

class AirpurifierUseCase @Inject constructor(
    private val airpurifierRepository: AirpurifierRepository
){
    suspend fun getAirpurifierStatus(deviceId: Long): AirpurifierResult {

        val data = airpurifierRepository.getAirpurifierStatus(deviceId)
        return data
    }

    suspend fun patchAirpurifierPower(deviceId: Long, activated:Boolean): PatchAirpurifierPowerResult {

        val data = airpurifierRepository.patchAirpurifierPower(deviceId, activated)
        return data
    }

    suspend fun patchAirpurifierFanMode(deviceId: Long, fanMode:String): PatchAirpurifierFanModeResult {

        val data = airpurifierRepository.patchAirpurifierFanMode(deviceId, fanMode)
        return data
    }
}