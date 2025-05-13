package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.data.LightData
import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.common.ErrorMessage
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.components.ColorWheelPicker
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.components.LightOtherSetting
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.components.LightSwitch
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.components.WatchBrightnessWithPureCompose
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchIntent
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.devices.components.LoadingDevice
import com.hogumiwarts.lumos.presentation.ui.viewmodel.LightViewModel
import com.hogumiwarts.lumos.presentation.ui.viewmodel.SwitchViewModel


@Composable
fun LightScreen(
    deviceId: Long,
    viewModel: LightViewModel = hiltViewModel()
) {


    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.sendIntent(LightIntent.LoadLightStatus(deviceId))
    }

    val state by viewModel.state.collectAsState()
    val isOn by viewModel.isOn.collectAsState()

    when(state){
        is LightStatusState.Error -> {
            when ((state as LightStatusState.Error).error) {
                CommonError.NetworkError -> ErrorMessage("인터넷 연결을 확인해주세요.")
                CommonError.UserNotFound -> ErrorMessage("사용자를 찾을 수 없습니다.")
                else -> ErrorMessage("알 수 없는 오류가 발생했습니다.")
            }
        }
        LightStatusState.Idle -> {}
        is LightStatusState.Loaded -> {
            val data = (state as LightStatusState.Loaded).data
            ScrollScreen(data.deviceName, isOn, data.brightness, deviceId=deviceId)
        }
        LightStatusState.Loading ->  LoadingDevice()
    }

}

@Composable
private fun ScrollScreen(name : String, isOn: Boolean, bright: Int, deviceId: Long,viewModel: LightViewModel = hiltViewModel()) {
    var screen by remember { mutableStateOf("switch") }// 현재 화면/다음 화면 상태

    var brightness by remember { mutableStateOf(bright) }
    // 화면 전환 시 애니메이션
    val switchOffsetY by animateDpAsState(
        targetValue = when (screen) {
            "switch" -> 0.dp
            "brightness" -> (-300).dp
            "color" -> (-600).dp
            "other" -> (-900).dp
            else -> 0.dp
        },
        animationSpec = tween(400),
        label = "switchOffset"
    )

    val brightnessOffsetY by animateDpAsState(
        targetValue = when (screen) {
            "switch" -> 300.dp
            "brightness" -> 0.dp
            "color" -> (-300).dp
            "other" -> (-600).dp
            else -> 0.dp
        },
        animationSpec = tween(400),
        label = "brightnessOffset"
    )

    val colorOffsetY by animateDpAsState(
        targetValue = when (screen) {
            "switch" -> 600.dp
            "brightness" -> 300.dp
            "color" -> 0.dp
            "other" -> (-300).dp
            else -> 600.dp
        },
        animationSpec = tween(400),
        label = "colorOffset"
    )


    val otherOffsetY by animateDpAsState(
        targetValue = when (screen) {
            "switch" -> 900.dp
            "brightness" -> 600.dp
            "color" -> 300.dp
            "other" -> 0.dp
            else -> 900.dp
        },
        animationSpec = tween(400),
        label = "otherOffset"
    )


    Box(modifier = Modifier
        .fillMaxSize()
        .clip(CircleShape)
        .background(Color(0xFF111322))) {

        // 1. LightSwitch
        Box(modifier = Modifier.offset(y = switchOffsetY)) {
            LightSwitch(
                deviceId = deviceId,
                isOn = isOn,
                name = name,
                onSwipeUp = { screen = "brightness" }
            )
        }

        // 2. Brightness Screen
        Box(modifier = Modifier.offset(y = brightnessOffsetY)) {
            WatchBrightnessWithPureCompose(
                brightness = brightness,
                onBrightnessChange = { brightness = it },
                onDragEnd = { sendBrightnessToServer(it,viewModel,deviceId) },
                onSwipeDown = { screen = "switch" },
                onSwipeUp = { screen = "color" }
            )
        }

        // 3. Color Picker
        Box(modifier = Modifier.offset(y = colorOffsetY)) {
            ColorWheelPicker(
                onSwipeDown = { screen = "brightness" },
                onSwipeUp = { screen = "other" }
            )
        }

        // 4. Other Setting
        Box(modifier = Modifier.offset(y = otherOffsetY)) {
            LightOtherSetting(
                onSwipeDown = { screen = "color" }
            )
        }


    }
}
fun sendBrightnessToServer(brightness: Int, viewModel: LightViewModel, deviceId: Long) {
    viewModel.sendIntent(LightIntent.ChangeLightBright(deviceId, brightness))
}


// 🧪 Wear OS 에뮬레이터에서 미리보기 지원
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
        LightScreen(1L)
    }
}