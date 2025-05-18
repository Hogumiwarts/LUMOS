package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.light.GetLightStatusResult
import com.hogumiwarts.domain.model.minibig.GetSwitchStatusResult
import com.hogumiwarts.domain.usecase.LightUseCase
import com.hogumiwarts.domain.usecase.SwitchUseCase
import com.hogumiwarts.domain.usecase.TokensUseCase
import com.hogumiwarts.lumos.ui.screens.Control.minibig.SwitchIntent
import com.hogumiwarts.lumos.ui.screens.Control.minibig.SwitchStatusState
import com.hogumiwarts.lumos.ui.screens.control.light.LightIntent
import com.hogumiwarts.lumos.ui.screens.control.light.LightStatusState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwitchViewModel  @Inject constructor(
    private val switchUseCase: SwitchUseCase, // 유즈케이스 주입
    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
): ViewModel() {
    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _state = MutableStateFlow<SwitchStatusState>(SwitchStatusState.Idle)
    val state: StateFlow<SwitchStatusState> = _state

    // 🔸 Intent를 받기 위한 SharedFlow (MVI 이벤트 트리거
    val intentFlow = MutableSharedFlow<SwitchIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    // 🔸 외부에서 Intent를 보낼 수 있도록 helper 함수 제공
    fun sendIntent(intent: SwitchIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is SwitchIntent.LoadSwitchStatus -> loadSwitchStatus(intent.deviceId)
                }
            }
        }
    }

    private fun loadSwitchStatus(deviceId: Long) {
        viewModelScope.launch {
            _state.value = SwitchStatusState.Loading

            when (val result = switchUseCase.getSwitchStatus(deviceId)) {

                is GetSwitchStatusResult.Error -> {
                    _state.value = SwitchStatusState.Error(result.error)
                }
                is GetSwitchStatusResult.Success -> {
                    val  data =result.data
                    _state.value = SwitchStatusState.Loaded(result.data)
                }
            }
        }
    }
}