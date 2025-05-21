package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.usecase.GestureUseCase
import com.hogumiwarts.lumos.ui.screens.gesture.GestureIntent
import com.hogumiwarts.lumos.ui.screens.gesture.GestureState
import com.hogumiwarts.lumos.ui.screens.gesture.GestureUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GestureViewModel @Inject constructor(
    private val gestureUseCase: GestureUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val channel = Channel<GestureIntent>()

    private val _state = MutableStateFlow<GestureState>(GestureState.Idle)
    val state: StateFlow<GestureState> = _state

    private val _uiState = MutableStateFlow<GestureUIState>(GestureUIState())
    val uiState: StateFlow<GestureUIState> = _uiState

    init {
        handleIntent()
    }

    private fun handleIntent() {
        viewModelScope.launch {
            channel.consumeAsFlow().collectLatest { // 새 값이 들어오면 이전 값 초기화 할때 사용
                when (it) {
                    GestureIntent.LoadGesture -> {
                        loadGesture()
                    }
                }
            }
        }
    }

    private fun loadGesture() {
        viewModelScope.launch {
            _state.value = GestureState.Loading

            when (val result = gestureUseCase.getGesture()) {

                is GestureResult.Error -> {
                    Log.d("Post", "getGestureListe: $result")
                    _state.value = GestureState.Error(result.error)
                }
                is GestureResult.Success -> {
                    Log.d("Post", "getGestureListd: $result")
                    _state.value = GestureState.LoadedGesture(result.data)
                }
            }
//            val image = gestureUseCase.getGesture()
//            if (image is GestureResult.Success) {
//                _uiState.update {
//                    it.copy(data = image.data)
//                }
//            }
        }


    }
}