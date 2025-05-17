package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.repository.GestureRepository
import com.hogumiwarts.domain.usecase.GestureUseCase
import com.hogumiwarts.lumos.ui.screens.Gesture.GestureIntent
import com.hogumiwarts.lumos.ui.screens.Gesture.GestureState
import com.hogumiwarts.lumos.ui.screens.Gesture.GestureUIState
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.AirpurifierStatusState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class GestureViewModel @Inject constructor(
    private val gestureUseCase: GestureUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<GestureState>(GestureState.Idle)
    val state: StateFlow<GestureState> = _state

    private val _uiState = MutableStateFlow<GestureUIState>(GestureUIState())
    val uiState: StateFlow<GestureUIState> = _uiState

    val intent = MutableSharedFlow<GestureIntent>()

    init {
        viewModelScope.launch {
            intent.collectLatest {
                when (it) {
                    GestureIntent.LoadGesture -> loadGesture()
                }
            }
        }
    }


    private fun loadGesture() {
        viewModelScope.launch {
            Timber.tag("gesture").d("🚀 loadGesture() 호출됨")

            _state.value = GestureState.Loading

            when (val result = gestureUseCase.getGesture()) {

                is GestureResult.Error -> {

                    _state.value = GestureState.Error(result.error)
                }

                is GestureResult.Success -> {

                    if (result.data.isEmpty()) {
                        Timber.tag("gesture").d("⚠️ 받아온 제스처 리스트가 비어있음!")
                    } else {
                        Timber.tag("gesture").d("✅ ViewModel에서 받은 제스처 수: ${result.data.size}")
                        result.data.forEach {
                            Timber.tag("gesture").d("📦 ${it.gestureId} / ${it.gestureName}")
                        }
                    }
                    _state.value = GestureState.LoadedGesture(result.data)
                }
                else -> {
                    Timber.tag("gesture").d("❓ 알 수 없는 타입: ${result::class.qualifiedName}")
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