package com.hogumiwarts.domain.model.audio

import com.hogumiwarts.domain.model.CommonError

sealed class AudioVolumeResult {
    data class Success(
        val data : AudioVolumeData
    ): AudioVolumeResult()

    data class Error(val error: CommonError) : AudioVolumeResult()
}