package com.hogumiwarts.lumos.ui.screens.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.foundation.Canvas
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.StrokeCap

@Composable
fun ArrowIndicator(
    azimuthDeg: Float,
    modifier: Modifier = Modifier,
    size: Dp = 120.dp
) {
    // Canvas 전체를 돌려 버리는 방식이 가장 간단
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .background(Color(0xFFEDEDED), CircleShape)   // 회색 원 배경
            .rotate(azimuthDeg)                           // 방위각만큼 회전
    ) {
        // 화살표(↑)는 기본적으로 위를 향하도록 그려 두고,
        // Box 자체를 rotate() 시켜서 방향을 맞춥니다.
        Canvas(Modifier.fillMaxSize()) {
            drawArrow(this)
        }
    }
}

/** 가운데에서 위쪽으로 뾰족하게 솟은 ‘^’ 모양 한 번만 그리는 헬퍼 */
private fun drawArrow(scope: DrawScope) = with(scope) {
    val w = size.minDimension              // 한 변
    val centerX = size.width / 2f
    val centerY = size.height / 2f

    val shaftLen = w * 0.30f               // 화살 막대 길이
    val headLen  = w * 0.15f               // 화살 머리 길이

    // 막대
    drawLine(
        color = Color(0xFF3E4784),
        start = androidx.compose.ui.geometry.Offset(centerX, centerY + shaftLen/2),
        end   = androidx.compose.ui.geometry.Offset(centerX, centerY - shaftLen/2),
        strokeWidth = w * 0.05f,
        cap = StrokeCap.Round
    )

    // 머리
    val path = Path().apply {
        moveTo(centerX, centerY - shaftLen/2 - headLen)          // 꼭짓점
        lineTo(centerX - headLen*0.6f, centerY - shaftLen/2)     // 좌측
        lineTo(centerX + headLen*0.6f, centerY - shaftLen/2)     // 우측
        close()
    }
    drawPath(path, Color(0xFF3E4784), style = Fill)
}