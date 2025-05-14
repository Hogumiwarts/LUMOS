package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult

interface AirpurifierRepository {

    suspend fun getAirpurifierStatus(deviceId: Long): AirpurifierResult

    suspend fun patchAirpurifierPower(deviceId: Long, activated: Boolean): PatchAirpurifierPowerResult

}