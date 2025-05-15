package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.lumos.domain.model.light.GetLightStatusResult
import com.hogumiwarts.lumos.domain.usecase.LightUseCase
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightIntent
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.control.ControlState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LightViewModel @Inject constructor(
    private val lightUseCase: LightUseCase, // 유즈케이스 주입
    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
) : ViewModel() {


    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _state = MutableStateFlow<LightStatusState>(LightStatusState.Idle)
    val state: StateFlow<LightStatusState> = _state

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _powerState = MutableStateFlow<ControlState>(ControlState.Idle)
    val powerState: StateFlow<ControlState> = _powerState

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _brightnessState = MutableStateFlow<ControlState>(ControlState.Idle)
    val brightnessState: StateFlow<ControlState> = _brightnessState


    private val _isOn = MutableStateFlow(false)
    val isOn: StateFlow<Boolean> = _isOn

    // 🔸 Intent를 받기 위한 SharedFlow (MVI 이벤트 트리거
    val intentFlow = MutableSharedFlow<LightIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    // 🔄 화면에서 보낸 Intent 수신 및 처리
    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is LightIntent.LoadLightStatus -> loadSwitchStatus(intent.deviceId)
                    is LightIntent.ChangeLightPower -> changeSwitchPower(intent.deviceId, intent.activated)
                    is LightIntent.ChangeLightBright -> patchLightBright(intent.deviceId, intent.brightness)
                    is LightIntent.ChangeLightColor -> patchLightColor(intent.deviceId,intent.color)
                }
            }
        }
    }

    // 🔸 외부에서 Intent를 보낼 수 있도록 helper 함수 제공
    fun sendIntent(intent: LightIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }

    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun loadSwitchStatus(deviceId: Long) {
        viewModelScope.launch {
            _state.value = LightStatusState.Loading

            when (val result = lightUseCase.getLightStatus(deviceId)) {
                is GetLightStatusResult.Success -> {
                    _state.value = LightStatusState.Loaded(result.data)
                    _isOn.value =result.data.activated
                }
                is GetLightStatusResult.Error -> {
                    _state.value = LightStatusState.Error(result.error)
                }
            }
        }
    }

    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun changeSwitchPower(deviceId: Long, activated: Boolean) {
        viewModelScope.launch {
            _powerState.value = ControlState.Loading

            when (val result = lightUseCase.patchLightPower(deviceId = deviceId, activated = activated)) {
                is PatchSwitchPowerResult.Success -> {
                    _powerState.value = ControlState.Loaded(result.data)
                    _isOn.value =activated
                }
                is PatchSwitchPowerResult.Error -> {
                    _powerState.value = ControlState.Error(result.error)
                }
            }
        }
    }

    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun patchLightBright(deviceId: Long, brightness: Int) {
        viewModelScope.launch {
            _brightnessState.value = ControlState.Loading

            when (val result = lightUseCase.patchLightBright(deviceId = deviceId, brightness = brightness)) {
                is PatchSwitchPowerResult.Success -> {
                    _brightnessState.value = ControlState.Loaded(result.data)
                }
                is PatchSwitchPowerResult.Error -> {
                    _brightnessState.value = ControlState.Error(result.error)
                }
            }
        }
    }

    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun patchLightColor(deviceId: Long, color: Int) {
        viewModelScope.launch {
            _brightnessState.value = ControlState.Loading

            when (val result = lightUseCase.patchLightColor(deviceId = deviceId, color = color)) {
                is PatchSwitchPowerResult.Success -> {
                    _brightnessState.value = ControlState.Loaded(result.data)
                }
                is PatchSwitchPowerResult.Error -> {
                    _brightnessState.value = ControlState.Error(result.error)
                }
            }
        }
    }




}