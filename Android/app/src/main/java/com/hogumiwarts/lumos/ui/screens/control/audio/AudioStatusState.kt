package com.hogumiwarts.lumos.ui.screens.control.audio

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.audio.AudioStatusData

sealed class AudioStatusState {
    object Idle : AudioStatusState()
    object Loading : AudioStatusState()
    data class Loaded(val data: AudioStatusData) : AudioStatusState()
    data class Error(val error: CommonError) : AudioStatusState()
}