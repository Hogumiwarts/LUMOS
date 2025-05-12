package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.domain.model.GetDevicesResult
import com.hogumiwarts.lumos.domain.model.GetSwitchStatusResult
import com.hogumiwarts.lumos.domain.usecase.DeviceUseCase
import com.hogumiwarts.lumos.domain.usecase.SwitchUseCase
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchStatusIntent
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceIntent
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwitchViewModel@Inject constructor(
    private val switchUseCase: SwitchUseCase, // 유즈케이스 주입
    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
) : ViewModel() {

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _state = MutableStateFlow<SwitchStatusState>(SwitchStatusState.Idle)
    val state: StateFlow<SwitchStatusState> = _state

    // 🔸 Intent를 받기 위한 SharedFlow (MVI 이벤트 트리거
    val intentFlow = MutableSharedFlow<SwitchStatusIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    // 🔄 화면에서 보낸 Intent 수신 및 처리
    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is SwitchStatusIntent.LoadSwitchStatus -> loadSwitchStatus(intent.deviceId)
                }
            }
        }
    }

    // 🔸 외부에서 Intent를 보낼 수 있도록 helper 함수 제공
    fun sendIntent(intent: SwitchStatusIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }

    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun loadSwitchStatus(deviceId: Long) {
        viewModelScope.launch {
            _state.value = SwitchStatusState.Loading

            when (val result = switchUseCase.getSwitchStatus(deviceId)) {
                is GetSwitchStatusResult.Success -> {
                    _state.value = SwitchStatusState.Loaded(result.data)

                }
                is GetSwitchStatusResult.Error -> {
                    _state.value = SwitchStatusState.Error(result.error)
                }
            }
        }
    }
}