package com.hogumiwarts.lumos.ui.screens.control.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * 그라데이션이 적용된 원형 진행 인디케이터
 */
@Composable
fun GradientCircularProgressIndicator(
    modifier: Modifier = Modifier,
    progress: Float = 0.70f,
    strokeWidth: Float,
    strokeCap: StrokeCap = StrokeCap.Round
) {

    // 그라데이션 색상 정의
    val gradientColors = listOf(
        Color(0xFFD8DDFF),
        Color(0xFF717BBC),
        Color(0xFF3E4784),
        Color(0xFFD8DDFF)
    )

    Canvas(modifier = modifier) {
        val diameter = size.minDimension
        val radius = diameter / 2 - strokeWidth / 2
        val startAngle = -90f  // 12시 방향에서 시작
        val sweepAngle = 360f * progress

        // 그라데이션 브러시 생성
        val brush = Brush.sweepGradient(
            colors = gradientColors,
            center = Offset(size.width / 2, size.height / 2)
        )


        drawArc(
            brush = brush,
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = Size(diameter - strokeWidth, diameter - strokeWidth),
            style = Stroke(width = strokeWidth, cap = strokeCap)
        )
    }
}
