package com.hogumiwarts.lumos.presentation.ui.screens.control.light.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.common.AnimatedToggleButton
import com.hogumiwarts.lumos.presentation.ui.screens.control.ControlState
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightIntent
import com.hogumiwarts.lumos.presentation.ui.viewmodel.LightViewModel

@Composable
fun LightSwitch(
    deviceId: Long,
    viewModel: LightViewModel = hiltViewModel(),
    isOn: Boolean, name: String,
    onSwipeUp: () -> Unit
) {
//    var isChecked by remember { mutableStateOf(exampleLight.activated) } // 전체 스위치 상태

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.animation_down)
    )

    val powerState by viewModel.powerState.collectAsState()


    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111322))
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (totalDrag < -50f) {
                            onSwipeUp() // 위로 스와이프 시 전환
                        }
                        totalDrag = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            }
    ) {

        val (title, toggle, arrow) = createRefs()

        // 텍스트: 상단 고정
        Text(
            text = name,
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 18.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toggle.top)
            }
        )



        // 토글 버튼: 화면 정중앙
        AnimatedToggleButton(
            isOn = isOn,
            onToggle = { viewModel.sendIntent(LightIntent.ChangeSwitchPower(deviceId, !isOn))},
            modifier = Modifier.constrainAs(toggle) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        // Lottie 애니메이션: 하단 고정
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(100.dp)
                .constrainAs(arrow) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(toggle.bottom)
                }
        )

        // 전환 중 로딩 표시
        when (powerState) {
            is ControlState.Error -> Unit
            ControlState.Idle -> Unit
            is ControlState.Loaded -> Unit // 상태 전환 후 별도 처리 없음
            ControlState.Loading -> {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}