package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightIntent

sealed class AudioIntent {
    data class LoadAudioStatus(val deviceId: Long): AudioIntent()
    data class LoadAudioPower(val deviceId: Long, val activated: Boolean): AudioIntent()
}