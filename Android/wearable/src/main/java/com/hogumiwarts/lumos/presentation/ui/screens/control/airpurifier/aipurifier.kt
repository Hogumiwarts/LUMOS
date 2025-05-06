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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import com.hogumiwarts.lumos.presentation.ui.common.OnOffSwitch


// 메인 화면 전환을 위한 상위 컴포저블
@Composable
fun Aipurifier() {
    var showNext by remember { mutableStateOf(false) } // 현재 화면/다음 화면 상태


    val nextOffsetY by animateDpAsState(
        targetValue = if (showNext) 0.dp else 300.dp,
        animationSpec = tween(400),
        label = "nextScreenOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 무드 플레이어 화면
        Box(modifier = Modifier.offset()) {
            AipurifierScreen(
                volumePercent = 40,
                isOn = true,
                onToggle = {},
                onSwipeUp = { showNext = true }
            )
        }

        // 음악 플레이어 화면
        if (showNext || nextOffsetY < 300.dp) {
            Box(modifier = Modifier.offset(x = nextOffsetY)) {
                AipurifierSetting(onSwipeDown = { showNext = false }) // 아래로 스와이프 시 복귀
            }
        }
    }
}


@Composable
fun AipurifierSetting(onSwipeDown: () -> Unit) {
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
            Text(text = "팬속도", fontSize = 16.sp, color = Color.White)
        }

        // 볼륨 및 스위치 토글
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(30.dp))
                .constrainAs(player) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            contentAlignment = Alignment.CenterStart
        ) {
        FanSpeedSelector()
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
                text = "적용하기",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
}
@Composable
fun FanSpeedSelector() {
    val options = listOf("Low", "Auto", "High")
    var selectedIndex by remember { mutableStateOf(1) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (totalDrag < -50f) {
                            selectedIndex = (selectedIndex + 1) % options.size
                        } else if (totalDrag > 50f) {
                            selectedIndex = (selectedIndex - 1 + options.size) % options.size
                        }
                        totalDrag = 0f
                    },
                    onHorizontalDrag =  { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 왼쪽
            Text(
                text = options[(selectedIndex - 1 + options.size) % options.size],
                fontSize = 18.sp,
                color = Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // 가운데
            Text(
                text = options[selectedIndex],
                fontSize = 28.sp,
                color = Color.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )

            // 오른쪽
            Text(
                text = options[(selectedIndex + 1) % options.size],
                fontSize = 18.sp,
                color = Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}



// 무드 플레이어 화면 (첫 번째 화면)
@Composable
fun AipurifierScreen(
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
    ) {
        val (title, toggle, arrow) = createRefs()

        // 제목
        Text(
            text = "교육장 공기 당담",
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
                .clickable {
                    // 클릭 시 동작
                    onSwipeUp()
                }
                .constrainAs(toggle) {
                    top.linkTo(parent.top)
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
                Column {
                    Text(
                        text = "펜속도  $volumePercent%",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "미세먼지 매우 좋음",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                OnOffSwitch(
                    checked = switchState.value,
                    onCheckedChange = {
                        switchState.value = it
                        onToggle(it)
                    }
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier.constrainAs(arrow) {
                top.linkTo(toggle.bottom)
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



// 미리보기 (NextScreen 기준)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun AirPreview() {
    Aipurifier()
}

