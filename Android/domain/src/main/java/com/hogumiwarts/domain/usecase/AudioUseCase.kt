package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.audio.AudioStatusResult
import com.hogumiwarts.domain.repository.AudioRepository
import javax.inject.Inject

class AudioUseCase@Inject constructor(
    private val audioRepository: AudioRepository
) {
    suspend fun getAudioStatus(deviceId: Long): AudioStatusResult {

        val data = audioRepository.getAudioStatus(deviceId)
        return data
    }
}