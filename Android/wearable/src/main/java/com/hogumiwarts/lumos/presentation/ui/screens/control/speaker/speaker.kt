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
fun MoodPlayerAnimatedContainer() {
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
            MoodPlayerScreen(
                volumePercent = 40,
                isOn = true,
                onToggle = {},
                onSwipeUp = { showNext = true }
            )
        }

        // 음악 플레이어 화면
        if (showNext || nextOffsetY < 300.dp) {
            Box(modifier = Modifier.offset(y = nextOffsetY)) {
                NextScreen(onSwipeDown = { showNext = false }) // 아래로 스와이프 시 복귀
            }
        }
    }
}

// 음악 플레이어 화면 (두 번째 화면)
@Composable
fun NextScreen(onSwipeDown: () -> Unit) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111322))
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (totalDrag > 50f) {
                            onSwipeDown() // 아래로 스와이프 시 화면 복귀
                        }
                        totalDrag = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            },
    ) {
        val (title, player, more) = createRefs()

        // 노래 제목 및 아티스트
        Column(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(player.top)
            },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "WISH", fontSize = 20.sp, color = Color.White)
            Text(text = "NCT WISH", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f))
        }

        // 음악 컨트롤 (이전/재생/다음)
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(player) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Previous",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Next",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }

        // 하단 안내 텍스트
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier.constrainAs(more) {
                top.linkTo(player.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Text(
                text = "폰에서 세부 제어",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}

// 무드 플레이어 화면 (첫 번째 화면)
@Composable
fun MoodPlayerScreen(
    volumePercent: Int = 40,
    isOn: Boolean = true,
    onToggle: (Boolean) -> Unit,
    onSwipeUp: () -> Unit
) {
    val switchState = remember { mutableStateOf(isOn) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.animation_down)
    )

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

        // 제목
        Text(
            text = "무드 플레이어",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 18.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toggle.top)
            }
        )

        // 볼륨 및 스위치 토글
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0x10FFFFFF))
                .constrainAs(toggle) {
                    top.linkTo(parent.top, margin = 18.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "볼륨  $volumePercent%",
                    color = Color.White,
                    fontSize = 16.sp
                )
                LabeledSwitch(
                    checked = switchState.value,
                    onCheckedChange = {
                        switchState.value = it
                        onToggle(it)
                    }
                )
            }
        }

        // 아래쪽 애니메이션 (예: 스와이프 유도 화살표)
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
    }
}

// ON/OFF 스위치 구현
@Composable
fun LabeledSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = checked, label = "switchTransition")

    // thumb 이동 애니메이션
    val thumbOffset by transition.animateDp(label = "thumbOffset") { isChecked ->
        if (isChecked) 30.dp else 5.dp
    }

    // 배경 색상 애니메이션
    val trackColor by transition.animateColor(label = "trackColor") { isChecked ->
        if (isChecked) Color(0xFF4CD964) else Color.DarkGray
    }

    Box(
        modifier = modifier
            .width(55.dp)
            .height(25.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        // ON/OFF 텍스트
        Text(
            text = if (checked) "ON" else "OFF",
            color = Color.White,
            fontSize = 10.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = if (checked) TextAlign.Start else TextAlign.End
        )

        // 스위치 thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .background(Color.White, shape = CircleShape)
        )
    }
}

// 미리보기 (NextScreen 기준)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun MoodPlayerPreview() {
    NextScreen({})
}

