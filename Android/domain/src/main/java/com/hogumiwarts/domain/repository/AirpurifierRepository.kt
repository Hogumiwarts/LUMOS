package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierFanModeResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult

interface AirpurifierRepository {

    suspend fun getAirpurifierStatus(deviceId: Long): AirpurifierResult

    suspend fun patchAirpurifierPower(deviceId: Long, activated: Boolean): PatchAirpurifierPowerResult

    suspend fun patchAirpurifierFanMode(deviceId: Long, fanMode: String): PatchAirpurifierFanModeResult

}