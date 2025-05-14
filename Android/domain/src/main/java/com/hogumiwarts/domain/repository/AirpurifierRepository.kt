package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult

interface AirpurifierRepository {

    suspend fun getAirpurifierStatus(deviceId: Long): AirpurifierResult
}