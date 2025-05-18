package com.hogumiwarts.lumos.ui.screens.control.audio

import com.hogumiwarts.lumos.ui.screens.control.minibig.SwitchIntent

sealed class AudioIntent {
    data class LoadAudioStatus(val deviceId: Long): AudioIntent()
}