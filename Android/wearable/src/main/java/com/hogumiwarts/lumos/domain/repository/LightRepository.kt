package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.lumos.domain.model.light.GetLightStatusResult

interface LightRepository {

    suspend fun getLightStatus(deviceId: Long): GetLightStatusResult
}