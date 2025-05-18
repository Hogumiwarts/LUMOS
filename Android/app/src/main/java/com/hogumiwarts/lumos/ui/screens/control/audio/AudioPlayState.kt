package com.hogumiwarts.lumos.ui.screens.control.audio

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.audio.AudioPowerData

sealed class AudioPlayState {
    object Idle : AudioPlayState()
    object Loading : AudioPlayState()
    data class Loaded(val data: AudioPowerData) : AudioPlayState()
    data class Error(val error: CommonError) : AudioPlayState()
}