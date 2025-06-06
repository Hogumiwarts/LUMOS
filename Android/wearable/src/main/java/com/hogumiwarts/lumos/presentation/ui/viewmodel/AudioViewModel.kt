package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.audio.AudioPowerResult
import com.hogumiwarts.domain.model.audio.AudioStatusData
import com.hogumiwarts.domain.model.audio.AudioStatusResult
import com.hogumiwarts.domain.model.audio.AudioVolumeResult
import com.hogumiwarts.domain.usecase.AirpurifierUseCase
import com.hogumiwarts.domain.usecase.AudioUseCase
import com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier.AirpurifierIntent
import com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier.AirpurifierStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.AudioIntent
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.AudioPowerState
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.AudioStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.AudioVolumeState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AudioViewModel@Inject constructor(
    private val audioUseCase: AudioUseCase, // 유즈케이스 주입
    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
): ViewModel()  {
    private val _state = MutableStateFlow<AudioStatusState>(AudioStatusState.Idle)
    val state: StateFlow<AudioStatusState> = _state

    private val _powerState = MutableStateFlow<AudioPowerState>(AudioPowerState.Idle)
    val powerState: StateFlow<AudioPowerState> = _powerState

    private val _volumeState = MutableStateFlow<AudioVolumeState>(AudioVolumeState.Idle)
    val volumeState: StateFlow<AudioVolumeState> = _volumeState

    private val _isPower = MutableStateFlow(false)
    val isPower: StateFlow<Boolean> = _isPower


    val intentFlow = MutableSharedFlow<AudioIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is AudioIntent.LoadAudioStatus -> loadAudioStatus(intent.deviceId)
                    is AudioIntent.LoadAudioPower -> loadAudioPower(intent.deviceId, intent.activated)
                    is AudioIntent.LoadAudioVolume -> loadAudioVolume(intent.deviceId, intent.volume)
                }
            }
        }
    }

    fun sendIntent(intent: AudioIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }

    private fun loadAudioStatus(deviceId: Long) {
        viewModelScope.launch {
            _state.value = AudioStatusState.Loading

            when (val result = audioUseCase.getAudioStatus(deviceId)) {
                is AudioStatusResult.Success -> {
                    _state.value = AudioStatusState.Loaded(result.data)
                }
                is AudioStatusResult.Error -> {
                    _state.value = AudioStatusState.Error(result.error)
                }
            }
        }
    }

    private fun loadAudioPower(deviceId: Long, activated: Boolean) {
        viewModelScope.launch {
            _powerState.value = AudioPowerState.Loading

            when (val result = audioUseCase.patchAudioPower(deviceId, activated = activated)) {
                is AudioPowerResult.Success -> {
                    _powerState.value = AudioPowerState.Loaded(result.data)
                    _isPower.value = activated
                }
                is AudioPowerResult.Error -> {
                    _powerState.value = AudioPowerState.Error(result.error)
                }
            }
        }
    }

    private fun loadAudioVolume(deviceId: Long, volume: Int) {
        viewModelScope.launch {
            _volumeState.value = AudioVolumeState.Loading

            when (val result = audioUseCase.patchAudioVolume(deviceId, volume = volume)) {
                is AudioVolumeResult.Success -> {
                    _volumeState.value = AudioVolumeState.Loaded(result.data)
                }
                is AudioVolumeResult.Error -> {
                    _volumeState.value = AudioVolumeState.Error(result.error)
                }
            }
        }
    }

}