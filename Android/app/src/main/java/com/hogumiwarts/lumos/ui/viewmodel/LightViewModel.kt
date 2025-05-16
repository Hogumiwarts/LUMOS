package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.light.GetLightStatusResult
import com.hogumiwarts.domain.model.light.LightBrightResult
import com.hogumiwarts.domain.model.light.LightColorResult
import com.hogumiwarts.domain.model.light.LightTemperatureResult
import com.hogumiwarts.domain.usecase.LightUseCase
import com.hogumiwarts.domain.usecase.TokensUseCase
import com.hogumiwarts.lumos.ui.screens.control.light.ControlState
import com.hogumiwarts.lumos.ui.screens.control.light.LightBrightState
import com.hogumiwarts.lumos.ui.screens.control.light.LightColorState
import com.hogumiwarts.lumos.ui.screens.control.light.LightIntent
import com.hogumiwarts.lumos.ui.screens.control.light.LightStatusState
import com.hogumiwarts.lumos.ui.screens.control.light.LightTemperatureState
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
    private val jwtUseCase: TokensUseCase,

    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
) : ViewModel() {


    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _state = MutableStateFlow<LightStatusState>(LightStatusState.Idle)
    val state: StateFlow<LightStatusState> = _state

    private val _brightState = MutableStateFlow<LightBrightState>(LightBrightState.Idle)
    val brightState: StateFlow<LightBrightState> = _brightState

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _color = MutableStateFlow("String")
    val color: StateFlow<String> = _color

    private val _saturation = MutableStateFlow(0f)
    val saturation: StateFlow<Float> = _saturation

    private val _brightness = MutableStateFlow(0)
    val brightness: StateFlow<Int> = _brightness

    private val _temperature = MutableStateFlow(0)
    val temperature: StateFlow<Int> = _temperature

    private val _hue = MutableStateFlow(0)
    val hue: StateFlow<Int> = _hue

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _powerState = MutableStateFlow<ControlState>(ControlState.Idle)
    val powerState: StateFlow<ControlState> = _powerState

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _brightnessState = MutableStateFlow<LightBrightState>(LightBrightState.Idle)
    val brightnessState: StateFlow<LightBrightState> = _brightnessState

    private val _colorState = MutableStateFlow<LightColorState>(LightColorState.Idle)
    val colorState: StateFlow<LightColorState> = _colorState

    private val _temperatureState = MutableStateFlow<LightTemperatureState>(LightTemperatureState.Idle)
    val temperatureState: StateFlow<LightTemperatureState> = _temperatureState


    private val _isOn = MutableStateFlow(false)
    val isOn: StateFlow<Boolean> = _isOn

    // 🔸 Intent를 받기 위한 SharedFlow (MVI 이벤트 트리거
    val intentFlow = MutableSharedFlow<LightIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    // hue -> Color
    fun toColor(hue: Int, saturation: Float):String{
        val hsv = floatArrayOf((hue*36/10).toFloat(), saturation/100, 1.toFloat()) // hue 0~360, sat/val 0~1
        val argb = android.graphics.Color.HSVToColor(hsv)
        val composeColor = Color(argb)

        val hex = "#%08X".format(argb)
        Log.d("TAG", "toColor: $hex")
        return hex
    }

    // 🔄 화면에서 보낸 Intent 수신 및 처리
    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    is LightIntent.LoadLightStatus -> loadSwitchStatus(intent.deviceId)
                    is LightIntent.ChangeLightPower -> changeSwitchPower(intent.deviceId, intent.activated)
                    is LightIntent.ChangeLightBright -> patchLightBright(intent.deviceId, intent.brightness)
                    is LightIntent.ChangeLightColor -> patchLightColor(intent.deviceId,intent.color,intent.saturation)
                    is LightIntent.ChangeLightTemperature -> patchLightTemperature(intent.deviceId,intent.temperature)
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
                    val  data =result.data
                    _state.value = LightStatusState.Loaded(result.data)
                    _isOn.value =data.activated
                    _color.value = toColor(data.hue,data.saturation)
                    _saturation.value = data.saturation
                    _hue.value = data.hue
                    _brightness.value = data.brightness
                    _temperature.value = data.lightTemperature
                    Log.d("TAG", "loadSwitchStatus: ${data.brightness}")
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
            _brightnessState.value = LightBrightState.Loading

            when (val result = lightUseCase.patchLightBright(deviceId = deviceId, brightness = brightness)) {

                is LightBrightResult.Error -> {
                    _brightnessState.value = LightBrightState.Error(result.error)
                }
                is LightBrightResult.Success -> {
                    _brightnessState.value = LightBrightState.Loaded(result.data)
                }
            }
        }
    }



    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun patchLightColor(deviceId: Long, color: Int,saturation: Float) {
        viewModelScope.launch {
            _colorState.value = LightColorState.Loading

            when (val result = lightUseCase.patchLightColor(deviceId = deviceId, color = color, saturation)) {
                is LightColorResult.Success -> {
                    _colorState.value = LightColorState.Loaded(result.data)
                }
                is LightColorResult.Error -> {
                    _colorState.value = LightColorState.Error(result.error)
                }

            }
        }
    }

    private fun patchLightTemperature(deviceId: Long, temperature: Int) {
        viewModelScope.launch {
            _temperatureState.value = LightTemperatureState.Loading

            when (val result = lightUseCase.patchLightTemperature(deviceId = deviceId, temperature = temperature)) {
                is LightTemperatureResult.Success -> {
                    _temperatureState.value = LightTemperatureState.Loaded(result.data)
                    _temperature.value = result.data.temperature
                }
                is LightTemperatureResult.Error -> {
                    _temperatureState.value = LightTemperatureState.Error(result.error)
                }

            }
        }
    }




}