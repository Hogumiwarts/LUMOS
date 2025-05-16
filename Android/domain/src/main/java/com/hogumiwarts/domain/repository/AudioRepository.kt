package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.audio.AudioPowerResult
import com.hogumiwarts.domain.model.audio.AudioStatusResult

interface AudioRepository {
    suspend fun getAudioStatus(deviceId: Long): AudioStatusResult

    suspend fun patchAudioPower(deviceId: Long, activated: Boolean): AudioPowerResult
}