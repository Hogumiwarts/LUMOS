package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierFanModeResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult

interface AirpurifierRepository {

    suspend fun getAirpurifierStatus(deviceId: Int): AirpurifierResult

    suspend fun patchAirpurifierPower(deviceId: Int, activated: Boolean): PatchAirpurifierPowerResult

    suspend fun patchAirpurifierFanMode(deviceId: Int, fanMode: String): PatchAirpurifierFanModeResult

}