package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.audio.AudioStatusData
import com.hogumiwarts.lumos.domain.model.light.LightStatusData
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightStatusState

sealed class AudioStatusState {
    object Idle : AudioStatusState()
    object Loading : AudioStatusState()
    data class Loaded(val data: AudioStatusData) : AudioStatusState()
    data class Error(val error: CommonError) : AudioStatusState()
}