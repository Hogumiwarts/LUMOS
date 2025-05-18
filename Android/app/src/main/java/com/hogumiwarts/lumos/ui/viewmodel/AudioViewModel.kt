package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.audio.AudioStatusResult
import com.hogumiwarts.domain.usecase.AirpurifierUseCase
import com.hogumiwarts.domain.usecase.AudioUseCase
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.AirpurifierIntent
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.AirpurifierStatusState
import com.hogumiwarts.lumos.ui.screens.control.audio.AudioIntent
import com.hogumiwarts.lumos.ui.screens.control.audio.AudioStatusState
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
) : ViewModel() {
    private val _state = MutableStateFlow<AudioStatusState>(AudioStatusState.Idle)
    val state: StateFlow<AudioStatusState> = _state

    val intentFlow = MutableSharedFlow<AudioIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is AudioIntent.LoadAudioStatus -> loadAudioStatus(intent.deviceId)
                }
            }
        }
    }

    fun sendIntent(intent: AudioIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }


    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun loadAudioStatus(deviceId: Long) {
        viewModelScope.launch {
            _state.value = AudioStatusState.Loading

            when (val result = audioUseCase.getAudioStatus(deviceId)) {


                is AudioStatusResult.Error -> {
                    _state.value = AudioStatusState.Error(result.error)
                }
                is AudioStatusResult.Success -> {
                    _state.value = AudioStatusState.Loaded(result.data)
                }
            }
        }
    }

}