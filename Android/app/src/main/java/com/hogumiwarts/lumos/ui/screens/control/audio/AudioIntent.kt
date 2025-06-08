package com.hogumiwarts.lumos.ui.screens.control.audio

import com.hogumiwarts.lumos.ui.screens.control.minibig.SwitchIntent

sealed class AudioIntent {
    data class LoadAudioStatus(val deviceId: Long): AudioIntent()
    data class LoadAudioPlay(val deviceId: Long, val play: Boolean): AudioIntent()
    data class LoadAudioVolume(val deviceId: Long, val volume: Int): AudioIntent()
}