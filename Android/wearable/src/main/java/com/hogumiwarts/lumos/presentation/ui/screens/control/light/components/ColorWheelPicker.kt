package com.hogumiwarts.lumos.presentation.ui.screens.control.light.components

import android.util.Log
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

@Composable
fun ColorWheelPicker(
    onSwipeDown: () -> Unit,
    onSwipeUp: () -> Unit,
    onColorSelected: (Float) -> Unit, // ✅ 손 뗐을 때 색상 전송용 콜백 추가
    color: Float,
    onColorChange: (Float) -> Unit,
) {

    var hue by remember { mutableFloatStateOf(color*36/10) } // 0~360
    val selectedColor = Color.hsv(hue.toFloat(), 1f, 1f)
    // Color 객체에서 RGB 값 추출
    val red = (selectedColor.red * 255).toInt()
    val green = (selectedColor.green * 255).toInt()
    val blue = (selectedColor.blue * 255).toInt()

    val hexColor = String.format("#%02X%02X%02X", red, green, blue)
//    Log.d("TAG", "ColorWheelPicker: $hexColor")

    Box(
        modifier = Modifier
            .size(250.dp)
            .padding(10.dp)
            .pointerInput(Unit) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val strokeWidth = 80f
                val radius = (minOf(size.width, size.height) - strokeWidth) / 2f

                val pointerRadius = 40f
                val touchThreshold = pointerRadius + 1000f

                detectDragGestures (
                    onDragEnd = {
                        val selectedColor = Color.hsv(hue.toFloat(), 1f, 1f)
                        val red = (selectedColor.red * 255).toInt()
                        val green = (selectedColor.green * 255).toInt()
                        val blue = (selectedColor.blue * 255).toInt()
                        val hexColor = String.format("#%02X%02X%02X", red, green, blue)

                        onColorSelected(hue*10/36) // ✅ 손 뗐을 때 호출
                    },
                    onDrag = { change, _ ->
                    val touchPoint = change.position

                    val angleRad = Math.toRadians(hue.toDouble())
                    val pointerX = center.x + cos(angleRad).toFloat() * radius
                    val pointerY = center.y + sin(angleRad).toFloat() * radius
                    val distance = Offset(pointerX, pointerY)
                        .minus(touchPoint)
                        .getDistance()

                    if (distance <= touchThreshold) {
                        val dx = touchPoint.x - center.x
                        val dy = touchPoint.y - center.y
                        var angle = atan2(dy, dx) * 180f / PI.toFloat()
                        if (angle < 0) angle += 360f
                        hue = angle
                        onColorChange(hue)
                    }
                })
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (totalDrag > 50f) onSwipeDown()
                        if (totalDrag < -50f) onSwipeUp()
                        totalDrag = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            }
        ) {
            val strokeWidth = 120f
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.minDimension - strokeWidth) / 2f

            // 1. 외곽 컬러 휠 (비어있는 중앙을 만들기 위해 Stroke)
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
            val angleRad = Math.toRadians(hue.toDouble())
            val pointerX = center.x + cos(angleRad).toFloat() * radius
            val pointerY = center.y + sin(angleRad).toFloat() * radius

            // 중앙이 비어있는 흰색 테두리 원
            drawCircle(
                color = Color.White,
                radius = 45f,
                center = Offset(pointerX, pointerY),
                style = Stroke(width = 10f) // 두께는 조절 가능
            )
        }

        // 중앙은 비워두거나, 아래처럼 강조용 미니 텍스트만 넣어도 좋음
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF111322)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "색상",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}



@Preview(showBackground = true, device = Devices.WEAR_OS_SMALL_ROUND)
@Composable
fun ColorWheelPickerPreview() {
//    ColorWheelPicker({} ,{},{})
}
