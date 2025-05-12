package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Text
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.data.LightData
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.common.AnimatedToggleButton
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.BedLightSwitch
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.MinibigScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.MoodPlayerContainer
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.MoodPlayerSwitch

val exampleLight = LightData(
    deviceId = 1L,
    tagNumber = 123456789L,
    deviceName = "무드등",
    deviceImg = "icon_light", // 또는 실제 이미지 URL/리소스 키
    lightColor = "#FFA500",   // 주황색 (ARGB Hex 형식)
    activated = true
)

@Composable
fun LightScreen(tagNumber: Long?) {
    var state by remember { mutableStateOf("switch") }// 현재 화면/다음 화면 상태

    // 화면 전환 시 애니메이션
    val switchOffsetY by animateDpAsState(
        targetValue = when (state) {
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
        targetValue = when (state) {
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
        targetValue = when (state) {
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
        targetValue = when (state) {
            "switch" -> 900.dp
            "brightness" -> 600.dp
            "color" -> 300.dp
            "other" -> 0.dp
            else -> 900.dp
        },
        animationSpec = tween(400),
        label = "otherOffset"
    )


    Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF111322))) {

        // 1. LightSwitch
        Box(modifier = Modifier.offset(y = switchOffsetY)) {
            LightSwitch(
                onSwipeUp = { state = "brightness" }
            )
        }

        // 2. Brightness Screen
        Box(modifier = Modifier.offset(y = brightnessOffsetY)) {
            WatchBrightnessWithPureCompose(
                onSwipeDown = { state = "switch" },
                onSwipeUp = { state = "color" }
            )
        }

        // 3. Color Picker
        Box(modifier = Modifier.offset(y = colorOffsetY)) {
            ColorWheelPicker(
                onSwipeDown = { state = "brightness" },
                onSwipeUp = {state = "other"}
            )
        }

        // 4. Other Setting
        Box(modifier = Modifier.offset(y = otherOffsetY)) {
            LightOtherSetting(
                onSwipeDown = { state = "color" }
            )
        }


    }

}




// 🧪 Wear OS 에뮬레이터에서 미리보기 지원
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
        LightScreen(1L)
    }
}