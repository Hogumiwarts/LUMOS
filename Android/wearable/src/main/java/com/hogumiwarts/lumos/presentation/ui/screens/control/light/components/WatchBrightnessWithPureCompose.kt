package com.hogumiwarts.lumos.presentation.ui.screens.control.light.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlendMode

@Composable
fun WatchBrightnessWithPureCompose(
    brightness: Int,
    onBrightnessChange: (Int) -> Unit,
    onDragEnd: (Int) -> Unit,
    onSwipeDown: () -> Unit,
    onSwipeUp: () -> Unit
) {
    var brightness by remember { mutableStateOf(brightness.toFloat()) } // 100%로 시작

    // 중심점 기준으로 각도를 계산하는 함수 - 수정된 버전
    fun calculateAngle(center: Offset, touchPoint: Offset): Float {
        val dx = touchPoint.x - center.x
        val dy = touchPoint.y - center.y

        // 각도를 라디안으로 계산
        var angle = atan2(dy, dx)

        // 라디안을 각도로 변환
        angle = (angle * 180f / PI).toFloat()

        // 각도가 -180~180 범위에서 0~360 범위로 변환
        if (angle < 0) angle += 360f

        // 디버깅 출력 (필요시)
        // println("원래 각도: $angle")

        return angle
    }

    // 각도를 진행률(0~100)로 변환 - 12시 방향(270도)이 0%가 되게 조정
    fun angleToProgress(angle: Float): Float {
        // 270도(12시 방향)를 기준으로 시계 방향으로 진행률 계산
        var progress = (angle - 270) / 360f * 100f

        // 음수 처리 (270도보다 작은 각도는 양수로 변환)
        if (progress < 0) progress += 100f

        // 디버깅 출력 (필요시)
        // println("각도: $angle, 진행률: $progress")

        return progress
    }

    Box(
        modifier = Modifier
            .size(300.dp)
            ,
        contentAlignment = Alignment.Center

    ) {
        // 외부 검은색 테두리
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(Color(0xFF111322))
        )

        // 진행 표시줄과 상호작용 영역
        Box(
            modifier = Modifier
                .size(250.dp)
                .padding(10.dp)
                .pointerInput(Unit) {
                    val center = Offset((size.width / 2).toFloat(), (size.height / 2).toFloat())
                    val strokeWidth = 120f
                    val radius = (minOf(size.width, size.height) - strokeWidth) / 2  // minDimension 대신

                    val pointerRadius = 50f
                    val touchThreshold = pointerRadius + 1000f // 여유 거리 포함

                    detectDragGestures (
                        onDragEnd = {
                            onDragEnd(brightness.toInt()) // 여기에서 호출
                        },
                        onDrag = {
                                change, _ ->
                            val touchPoint = change.position
                            val angle = calculateAngle(center, touchPoint)
                            val progress = angleToProgress(angle)

                            // 현재 포인터 위치 계산
                            val pointerAngleRad = Math.toRadians((270 + 3.6f * brightness).toDouble())
                            val pointerX = center.x + cos(pointerAngleRad).toFloat() * radius
                            val pointerY = center.y + sin(pointerAngleRad).toFloat() * radius
                            val distanceToPointer = Offset(pointerX, pointerY).minus(touchPoint).getDistance()
                            brightness = progress
                            onBrightnessChange(brightness.toInt())
                            // 포인터 근처에서만 조절 허용
                            if (distanceToPointer <= touchThreshold) {

                            }
                        }
                    )
                }

            ,
            contentAlignment = Alignment.Center
        ) {
            // 원형 그라데이션 진행 표시줄
            Canvas(modifier = Modifier.fillMaxSize()
                .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (totalDrag > 50f) {
                            onSwipeDown() // 아래로 스와이프 시 화면 복귀
                        }
                        if (totalDrag < -50f) {
                            onSwipeUp() // 위로 스와이프 시 전환
                        }
                        totalDrag = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            }) {
                val strokeWidth = 120f
                val centerX = size.width / 2
                val centerY = size.height / 2
                val radius = (size.minDimension - strokeWidth) / 2

                // 배경 원 (진행되지 않은 부분)
                drawCircle(
                    color = Color.White, // 흰색 배경
                    style = Stroke(width = strokeWidth),
                    radius = radius
                )

                // 그라데이션 색상
                val colors = listOf(
                    Color(0xFFF2F0E4), // 크림색
                    Color(0xFFF7D269), // 황금색
                    Color(0xFFF2F0E4)  // 다시 크림색으로 돌아감
                )

                // Canvas 전체를 회전시켜 그라데이션 시작점을 270도(12시 방향)로 조정
                rotate(degrees = 270f) {
                    val creamGoldGradient = Brush.sweepGradient(
                        colors = colors,
                        center = Offset(centerX, centerY)
                    )

                    // 현재 진행도에 맞는 호 계산
                    val sweepAngle = 3.6f * brightness // 진행률에 따른 각도 (100% = 360도)

                    // 그라데이션 진행 호 그리기 - rotate 안에서는 0도에서 시작 (회전이 적용되어 12시 방향이 됨)
                    drawArc(
                        brush = creamGoldGradient,
                        startAngle = 0f, // rotate 블록 안에서는 0도 (실제로는 270도)
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset(centerX - radius, centerY - radius)
                    )
                }

                // 포인터(흰색 원) 위치 계산 - 12시 방향(270도) 기준
                val angle = (270 + 3.6f * brightness) * PI / 180 // 라디안으로 변환
                val x = centerX + cos(angle).toFloat() * radius
                val y = centerY + sin(angle).toFloat() * radius

                // 방법 1: 여러 개의 원을 겹쳐서 고급 그림자 효과 만들기
                val pointerRadius = 40f
                drawCircle(
                    color = Color(0x70000000),
                    radius = pointerRadius,
                    center = Offset(x + 2f, y + 2f) // 오른쪽 아래로 이동
                )

                // 흰색 포인터 그리기
                drawCircle(
                    color = Color.White,
                    radius = pointerRadius,
                    center = Offset(x, y)
                )

            }

            // 중앙 어두운 원
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A1E2A)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${brightness.toInt()}%",
                        color = Color.White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

//
//@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
//@Composable
//fun WatchBrightnessWithPureComposePreview() {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        color = Color.Gray
//    ) {
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.Center
//        ) {
//            WatchBrightnessWithPureCompose(10,{}, onSwipeUp = {})
//
//        }
//    }
//}


@Composable
fun MultiLayerShadowEffect() {
    Box(modifier = Modifier.size(150.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 20

            // 여러 레이어의 그림자
            for (i in 1..5) {
                val alpha = 0.1f - (i * 0.015f)  // 바깥쪽으로 갈수록 투명해짐
                drawCircle(
                    color = Color.Black.copy(alpha = alpha),
                    radius = radius + (i * 3),
                    center = Offset(center.x + 5f, center.y + 5f),
                    blendMode = BlendMode.SrcOver
                )
            }

            // 테두리와 내부 구분을 위한 원 테두리
            drawCircle(
                color = Color.LightGray,
                radius = radius,
                center = center,
                style = Stroke(width = 2f)
            )

            // 메인 원
            drawCircle(
                color = Color.White,
                radius = radius,
                center = center
            )
        }
    }
}

@Composable
fun GradientWithHighlightEffect() {
    Box(modifier = Modifier.size(150.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.minDimension / 2 - 20

            // 배경 그림자
            drawCircle(
                color = Color(0x30000000),
                radius = radius + 5f,
                center = Offset(center.x + 7f, center.y + 7f)
            )

            // 메인 원 - 그라데이션으로 입체감 표현
            val gradient = Brush.radialGradient(
                colors = listOf(
                    Color.White,
                    Color(0xFFF0F0F0),
                    Color(0xFFE0E0E0)
                ),
                center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
                radius = radius * 1.5f
            )

            drawCircle(
                brush = gradient,
                radius = radius,
                center = center
            )

            // 하이라이트 (왼쪽 위)
            drawCircle(
                color = Color.White.copy(alpha = 0.7f),
                radius = radius * 0.4f,
                center = Offset(center.x - radius * 0.3f, center.y - radius * 0.3f),
                blendMode = BlendMode.Screen  // 밝게 하는 효과
            )
        }
    }
}