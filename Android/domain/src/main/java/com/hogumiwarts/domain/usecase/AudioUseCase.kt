package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.audio.AudioPowerResult
import com.hogumiwarts.domain.model.audio.AudioStatusResult
import com.hogumiwarts.domain.model.audio.AudioVolumeResult
import com.hogumiwarts.domain.repository.AudioRepository
import javax.inject.Inject

class AudioUseCase@Inject constructor(
    private val audioRepository: AudioRepository
) {
    suspend fun getAudioStatus(deviceId: Long): AudioStatusResult {

        val data = audioRepository.getAudioStatus(deviceId)
        return data
    }

    suspend fun patchAudioPower(deviceId: Long, activated: Boolean): AudioPowerResult {

        val data = audioRepository.patchAudioPower(deviceId, activated = activated)
        return data
    }

    suspend fun patchAudioVolume(deviceId: Long, volume: Int): AudioVolumeResult {

        val data = audioRepository.patchAudioVolume(deviceId, volume = volume)
        return data
    }
}