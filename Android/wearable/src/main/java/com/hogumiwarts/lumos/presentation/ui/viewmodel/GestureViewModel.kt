package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.gesture.GestureDetailResult
import com.hogumiwarts.domain.usecase.GestureUseCase
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureDetailState
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestureViewModel@Inject constructor(
    private val gestureUseCase: GestureUseCase, // 유즈케이스 주입
    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
): ViewModel() {


    private val _state = MutableStateFlow<GestureDetailState>(GestureDetailState.Idle)
    val state: StateFlow<GestureDetailState> = _state

    val intentFlow = MutableSharedFlow<GestureIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is GestureIntent.LoadGestureDetail -> loadAudioStatus(intent.deviceId)
                }
            }
        }
    }

    fun sendIntent(intent: GestureIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }

    private fun loadAudioStatus(deviceId: Long) {
        viewModelScope.launch {
            _state.value = GestureDetailState.Loading

            when (val result = gestureUseCase.getGestureDetail(deviceId)) {

                is GestureDetailResult.Error -> {
                    _state.value = GestureDetailState.Error(result.error)
                }
                is GestureDetailResult.Success -> {
                    _state.value = GestureDetailState.Loaded(result.data)
                }
            }
        }
    }

}