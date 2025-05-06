package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Text
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.wear.compose.material.Icon


// 메인 화면 전환을 위한 상위 컴포저블
@Composable
fun MoodPlayerScreen(tagNumber: Long) {
    var showNext by remember { mutableStateOf(false) } // 현재 화면/다음 화면 상태

    // 화면 전환 시 애니메이션
    val currentOffsetY by animateDpAsState(
        targetValue = if (showNext) (-300).dp else 0.dp,
        animationSpec = tween(400),
        label = "currentScreenOffset"
    )
    val nextOffsetY by animateDpAsState(
        targetValue = if (showNext) 0.dp else 300.dp,
        animationSpec = tween(400),
        label = "nextScreenOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 무드 플레이어 화면
        Box(modifier = Modifier.offset(y = currentOffsetY)) {
            MoodPlayerSwitch(
                volumePercent = 40,
                isOn = true,
                onToggle = {},
                onSwipeUp = { showNext = true }
            )
        }

        // 음악 플레이어 화면
        if (showNext || nextOffsetY < 300.dp) {
            Box(modifier = Modifier.offset(y = nextOffsetY)) {
                MoodPlayerContainer(onSwipeDown = { showNext = false }) // 아래로 스와이프 시 복귀
            }
        }
    }
}



// ON/OFF 스위치 구현


// 미리보기 (NextScreen 기준)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun MoodPlayerPreview() {
    MoodPlayerScreen(1L)
}

