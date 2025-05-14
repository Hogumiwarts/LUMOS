package com.hogumiwarts.lumos.presentation.ui.screens.control.light

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun ColorWheelPicker(
    onSwipeDown: () -> Unit,
    onSwipeUp: () -> Unit
) {
    var hue by remember { mutableFloatStateOf(0f) } // 0~360
    val saturation = 1f
    val brightness = 1f

    val selectedColor = Color.hsv(hue, saturation, brightness)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111322))
            .padding(10.dp)
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectDragGestures(
                    onDragEnd = {
                        if (totalDrag > 50f) {
                            onSwipeDown()
                        }
                        if (totalDrag < -50f) {
                            onSwipeUp() // 위로 스와이프 시 전환
                        }
                        totalDrag = 0f
                    },
                    onDrag = { change, dragAmount ->
                        totalDrag += dragAmount.y

                        val size = this.size
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val position = change.position

                        val strokeWidth = 90f
                        val radius = (minOf(size.width, size.height) - strokeWidth) / 2f

                        val pointerAngleRad = Math.toRadians(hue.toDouble())
                        val pointerX = center.x + cos(pointerAngleRad).toFloat() * radius
                        val pointerY = center.y + sin(pointerAngleRad).toFloat() * radius
                        val pointerPos = Offset(pointerX, pointerY)

                        val threshold = 50f
                        val distance = (pointerPos - position).getDistance()

                        if (distance <= threshold) {
                            val dx = position.x - center.x
                            val dy = position.y - center.y
                            var angle = atan2(dy, dx) * 180f / PI.toFloat()
                            if (angle < 0) angle += 360f
                            hue = angle
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(250.dp)) {
            val strokeWidth = 120f
            val radius = (minOf(size.width, size.height) - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)

            // 1. 컬러 휠 (무지개)
            drawCircle(
                brush = Brush.sweepGradient(
                    listOf(
                        Color.Red,
                        Color.Yellow,
                        Color.Green,
                        Color.Cyan,
                        Color.Blue,
                        Color.Magenta,
                        Color.Red
                    )
                ),
                center = center,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // 2. 포인터 위치
            val pointerAngleRad = Math.toRadians(hue.toDouble())
            val pointerX = center.x + cos(pointerAngleRad).toFloat() * radius
            val pointerY = center.y + sin(pointerAngleRad).toFloat() * radius

            drawCircle(
                color = Color.White,
                radius = 50f,
                center = Offset(pointerX, pointerY),
                style = Stroke(width = 10f)
            )
        }

        // 3. 중앙 색상 미리보기
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xffFFFFFF).copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "색상", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}


@Preview(showBackground = true, device = Devices.WEAR_OS_SMALL_ROUND)
@Composable
fun ColorWheelPickerPreview() {
    ColorWheelPicker({} ,{})
}
