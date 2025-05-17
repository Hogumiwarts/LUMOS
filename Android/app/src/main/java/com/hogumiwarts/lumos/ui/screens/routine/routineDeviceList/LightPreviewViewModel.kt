package com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hogumiwarts.domain.model.routine.CommandData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

// 루틴 생성 전용 뷰모델 (기기 미제어)
data class LightRoutineControlState(
    val power: Boolean = false,
    val brightness: Int = 50,
    val hue: Float = 0f,
    val saturation: Float = 0f,
)

@HiltViewModel
class LightPreviewViewModel @Inject constructor() : ViewModel() {

    private val _isOn = MutableStateFlow(false)
    val isOn: StateFlow<Boolean> = _isOn

    private val _brightness = MutableStateFlow(50)
    val brightness: StateFlow<Int> = _brightness

    private val _hue = MutableStateFlow(180f)
    val hue: StateFlow<Float> = _hue

    private val _saturation = MutableStateFlow(100f)
    val saturation: StateFlow<Float> = _saturation


    fun setPower(on: Boolean) {
        _isOn.value = on
    }

    fun setBrightness(level: Int) {
        _brightness.value = level
    }

    fun setColor(hue: Float, saturation: Float) {
        _hue.value = hue
        _saturation.value = saturation
    }

    fun setInitialState(isOn: Boolean, brightness: Int, hue: Float, saturation: Float) {
        _isOn.value = isOn
        _brightness.value = brightness
        _hue.value = hue
        _saturation.value = saturation
    }
}