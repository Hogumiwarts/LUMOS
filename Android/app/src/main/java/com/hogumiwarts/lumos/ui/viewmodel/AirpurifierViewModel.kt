package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierFanModeResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult
import com.hogumiwarts.domain.usecase.AirpurifierUseCase
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.AirpurifierFanModeState
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.AirpurifierIntent
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.AirpurifierPowerState
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.AirpurifierStatusState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AirpurifierViewModel@Inject constructor(
    private val airpurifierUseCase: AirpurifierUseCase, // 유즈케이스 주입
    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
) : ViewModel() {


    private val _isOn = MutableStateFlow(false)
    val isOn: StateFlow<Boolean> = _isOn

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _state = MutableStateFlow<AirpurifierStatusState>(AirpurifierStatusState.Idle)
    val state: StateFlow<AirpurifierStatusState> = _state

    private val _powerState = MutableStateFlow<AirpurifierPowerState>(AirpurifierPowerState.Idle)
    val powerState: StateFlow<AirpurifierPowerState> = _powerState

    private val _fanModeState = MutableStateFlow<AirpurifierFanModeState>(AirpurifierFanModeState.Idle)
    val fanModeState: StateFlow<AirpurifierFanModeState> = _fanModeState

    // 🔸 Intent를 받기 위한 SharedFlow (MVI 이벤트 트리거
    val intentFlow = MutableSharedFlow<AirpurifierIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    // 🔄 화면에서 보낸 Intent 수신 및 처리
    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is AirpurifierIntent.LoadAirpurifierStatus -> loadAirpurifierStatus(intent.deviceId)
                    is AirpurifierIntent.ChangeAirpurifierPower -> changeAirpurifierPower(intent.deviceId, intent.activated)
                    is AirpurifierIntent.ChangeAirpurifierFenMode -> changeAirpurifierFanMode(intent.deviceId, intent.fanMode)
                }
            }
        }
    }

    // 🔸 외부에서 Intent를 보낼 수 있도록 helper 함수 제공
    fun sendIntent(intent: AirpurifierIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }


    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun loadAirpurifierStatus(deviceId: Int) {
        viewModelScope.launch {
            _state.value = AirpurifierStatusState.Loading

            when (val result = airpurifierUseCase.getAirpurifierStatus(deviceId)) {
                is AirpurifierResult.Success -> {
                    _state.value = AirpurifierStatusState.Loaded(result.data)
                    _isOn.value = result.data.activated
                }
                is AirpurifierResult.Error -> {
                    _state.value = AirpurifierStatusState.Error(result.error)
                }
            }
        }
    }

    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun changeAirpurifierPower(deviceId: Int, activated: Boolean) {
        viewModelScope.launch {
            _powerState.value = AirpurifierPowerState.Loading

            when (val result = airpurifierUseCase.patchAirpurifierPower(deviceId,activated)) {
                is PatchAirpurifierPowerResult.Success -> {
                    _powerState.value = AirpurifierPowerState.Loaded(result.data)
                    _isOn.value =activated
                }
                is PatchAirpurifierPowerResult.Error -> {
                    _powerState.value = AirpurifierPowerState.Error(result.error)
                }
            }
        }
    }


    private fun changeAirpurifierFanMode(deviceId: Int, fanMode: String) {
        viewModelScope.launch {
            _fanModeState.value = AirpurifierFanModeState.Loading

            when (val result = airpurifierUseCase.patchAirpurifierFanMode(deviceId,fanMode)) {
                is PatchAirpurifierFanModeResult.Success -> {
                    _fanModeState.value = AirpurifierFanModeState.Loaded(result.data)
                }
                is PatchAirpurifierFanModeResult.Error -> {
                    _fanModeState.value = AirpurifierFanModeState.Error(result.error)
                }
            }
        }
    }

}