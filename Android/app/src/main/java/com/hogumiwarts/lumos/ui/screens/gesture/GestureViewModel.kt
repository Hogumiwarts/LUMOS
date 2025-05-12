package com.hogumiwarts.lumos.ui.screens.gesture

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.repository.GestureRepository
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
    private val gestureRepository: GestureRepository,
    @ApplicationContext private val context: Context
) : ViewModel(){

    val channel = Channel<GestureIntent>()

    private val _state = MutableStateFlow<GestureState>(GestureState.Idle)
    val state: StateFlow<GestureState> = _state

    init {
        handleIntent()
    }

    private fun handleIntent(){
        viewModelScope.launch {
            channel.consumeAsFlow().collectLatest { // 새 값이 들어오면 이전 값 초기화 할때 사용
                when(it){
                    GestureIntent.LoadGesture -> {
                        loadGesture()
                    }
                }
            }
        }
    }

    private fun loadGesture(){
        viewModelScope.launch {
            _state.value =GestureState.Loading
            val image = gestureRepository.getGestureList()
        }
    }


}