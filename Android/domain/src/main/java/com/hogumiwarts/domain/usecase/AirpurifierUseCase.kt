package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.repository.AirpurifierRepository
import javax.inject.Inject

class AirpurifierUseCase @Inject constructor(
    private val airpurifierRepository: AirpurifierRepository
){
    suspend fun getAirpurifierStatus(deviceId: Long): AirpurifierResult {

        val data = airpurifierRepository.getAirpurifierStatus(deviceId)
        return data
    }
}