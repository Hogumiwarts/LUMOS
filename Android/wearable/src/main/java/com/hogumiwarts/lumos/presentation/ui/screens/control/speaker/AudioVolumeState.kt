package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.audio.AudioStatusData
import com.hogumiwarts.domain.model.audio.AudioVolumeData

sealed class AudioVolumeState {
    object Idle : AudioVolumeState()
    object Loading : AudioVolumeState()
    data class Loaded(val data: AudioVolumeData) : AudioVolumeState()
    data class Error(val error: CommonError) : AudioVolumeState()
}