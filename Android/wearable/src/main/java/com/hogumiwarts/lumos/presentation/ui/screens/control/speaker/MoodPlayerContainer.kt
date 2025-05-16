package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R


@Composable
fun MoodPlayerContainer(onSwipeDown: () -> Unit) {

    var volumePercent by remember { mutableIntStateOf(40) }
    var isDraggingVolume by remember { mutableStateOf(false) }
    var dragStartPosition by remember { mutableStateOf(Offset.Zero) }
    var totalVerticalDrag by remember { mutableFloatStateOf(0f) }

    // 햅틱 피드백을 위한 현재 뷰 가져오기
    val view = LocalView.current

    // 볼륨 조절을 위한 드래그 거리 누적값
    var accumulatedVolumeGesture by remember { mutableFloatStateOf(0f) }
    // 임계값을 더 작게 설정
    val volumeThreshold = 2f
    // 한 번에 변경할 볼륨의 양
    val volumeStep = 3

    // 이전 볼륨 값 추적 (변경 여부 확인용)
    var prevVolumePercent by remember { mutableIntStateOf(volumePercent) }
    var isPlaying by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        dragStartPosition = offset
                        isDraggingVolume = offset.x > size.width * 0.5f
                        totalVerticalDrag = 0f
                        accumulatedVolumeGesture = 0f
                        // 드래그 시작 시 현재 볼륨 저장
                        prevVolumePercent = volumePercent
                    },
                    onDragEnd = {
                        if (totalVerticalDrag < -50f && !isDraggingVolume) {
                            onSwipeDown()
                        }
                        isDraggingVolume = false
                        totalVerticalDrag = 0f
                        accumulatedVolumeGesture = 0f
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()

                        // 전체 드래그 거리 누적 (화면 전환용)
                        totalVerticalDrag += dragAmount.y

                        // 볼륨 조절 중일 때만 볼륨 처리
                        if (isDraggingVolume) {
                            // 드래그 거리 누적 - 민감도 증가를 위해 1.5배 증폭
                            accumulatedVolumeGesture += dragAmount.y * 1.5f

                            // 이전 볼륨값 저장
                            val oldVolume = volumePercent

                            // 누적 드래그 거리가 임계값을 초과하면 볼륨 증가
                            if (accumulatedVolumeGesture >= volumeThreshold) {
                                // 볼륨을 volumeStep만큼 증가 (최대 100)
                                volumePercent = (volumePercent + volumeStep).coerceAtMost(100)
                                // 누적값 리셋 (임계값만큼 차감)
                                accumulatedVolumeGesture -= volumeThreshold
                            }
                            // 누적 드래그 거리가 -임계값 미만이면 볼륨 감소
                            else if (accumulatedVolumeGesture <= -volumeThreshold) {
                                // 볼륨을 volumeStep만큼 감소 (최소 0)
                                volumePercent = (volumePercent - volumeStep).coerceAtLeast(0)
                                // 누적값 리셋 (임계값만큼 증가)
                                accumulatedVolumeGesture += volumeThreshold
                            }

                            // 볼륨이 변경되었으면 햅틱 피드백 실행
                            if (volumePercent != oldVolume) {
                                // 진동 발생 (짧은 탭 느낌)
                                view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                            }
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {

        // 배경
        Image(
            painter = painterResource(id = R.drawable.wish),
            contentDescription = null,
            alpha = 0.6f,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
        )

        // 볼륨 값 표시 (드래그 중에만 표시)
        if (isDraggingVolume) {
            Box(
                modifier = Modifier.padding(8.dp)
            ) {
                CircularVolumeIndicator(
                    volumePercent = volumePercent,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // 볼륨 값 테스트용
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.CenterEnd
//            ) {
//                Text(
//                    text = "$volumePercent%",
//                    color = Color(0xFFFCEF95),  // 노란색
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.padding(end = 24.dp)
//                )
//            }
        }

        // 노래 제목 및 아티스트
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Column() {
                Text(
                    text = "NCT WISH", fontSize = 14.sp,
                    fontWeight = FontWeight.Light
                )

                Text(text = "WISH", fontSize = 20.sp, color = Color.White)
            }


            if (isPlaying) {
                // 재생 중이면 일시정지 아이콘 표시
                Icon(
                    painter = painterResource(id = R.drawable.ic_pause),
                    contentDescription = "일시정지",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isPlaying = !isPlaying
                        }
                )
            } else {
                // 일시정지 중이면 재생 아이콘 표시
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "재생",
                    tint = Color.White,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isPlaying = !isPlaying
                        }
                )
            }


            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x20F9F9F9),
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
}

@Composable
fun CircularVolumeIndicator(
    volumePercent: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val center = Offset(canvasWidth / 2, canvasHeight / 2)
        val radius = minOf(canvasWidth, canvasHeight) / 2 - 4.dp.toPx()

        // 시작 각도: 0도 (오른쪽), 끝 각도: 120도 (오른쪽에서 위쪽 일부까지)
        val startAngle = -60f  // 시작 각도
        val totalSweepAngle = 120f  // 전체 호의 각도
        val volumeSweepAngle = totalSweepAngle * volumePercent / 100f  // 볼륨에 따른 호의 각도

        // 1. 먼저 전체 호를 회색으로 그림 (배경)
        drawArc(
            color = Color(0xFF333333),  // 회색
            startAngle = startAngle,
            sweepAngle = totalSweepAngle,  // 전체 120도를 회색으로
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        // 2. 그 위에 볼륨에 해당하는 부분만 노란색으로 그림
        drawArc(
            color = Color(0xFFFCEF95),  // 노란색
            startAngle = startAngle,
            sweepAngle = volumeSweepAngle,  // 볼륨 퍼센트에 따른 부분만 노란색으로
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(
                width = 8.dp.toPx(),
                cap = StrokeCap.Round
            )
        )
    }
}