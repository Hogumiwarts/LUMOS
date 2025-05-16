package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.audio.AudioPowerData
import com.hogumiwarts.domain.model.audio.AudioStatusData

sealed class AudioPowerState {
    object Idle : AudioPowerState()
    object Loading : AudioPowerState()
    data class Loaded(val data: AudioPowerData) : AudioPowerState()
    data class Error(val error: CommonError) : AudioPowerState()
}