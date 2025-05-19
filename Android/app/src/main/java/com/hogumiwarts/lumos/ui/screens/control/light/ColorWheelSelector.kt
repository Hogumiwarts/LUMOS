package com.hogumiwarts.lumos.ui.screens.control.light

// Compose 상태 관리
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

// Compose 기본 UI
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

// 레이아웃 구성
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip

// 그래픽 및 드로잉
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

// 입력 처리
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import com.hogumiwarts.lumos.ui.screens.control.audio.SpeakerScreen

// 수학 함수
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI
// Android의 HSV 변환용 Color
import android.graphics.Color as AndroidColor

// Compose Color 별칭 (선택 사항)
import androidx.compose.ui.graphics.Color as ComposeColor




@Composable
fun ColorWheelSelector(
    modifier: Modifier = Modifier,
    onColorSelected: (Color) -> Unit
) {
    val radius = 150f
    val strokeWidth = 30f
    val angleState = remember { mutableFloatStateOf(0f) }
    val selectedColor = remember { mutableStateOf(Color.Red) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size((radius * 2).dp)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val center = Offset((size.width / 2).toFloat(), (size.height / 2).toFloat())
                    val angle = atan2(
                        change.position.y - center.y,
                        change.position.x - center.x
                    ) * (180f / Math.PI).toFloat()
                    val normalizedAngle = (angle + 360f) % 360f
                    angleState.value = normalizedAngle
                    selectedColor.value = hsvToColor(normalizedAngle, 1f, 1f)
                    onColorSelected(selectedColor.value)
                }
            }
        ) {
            // 색상 원
            drawColorWheel(radius, strokeWidth)

            // 핸들 위치 계산
            val theta = Math.toRadians(angleState.value.toDouble())
            val handleX = (size.width / 2 + cos(theta) * radius).toFloat()
            val handleY = (size.height / 2 + sin(theta) * radius).toFloat()

            // 핸들 원
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = Offset(handleX, handleY),
                style = Stroke(width = 3f)
            )
        }

        // 중앙 색상 미리보기
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(selectedColor.value)
        )
    }
}
fun hsvToColor(hue: Float, saturation: Float, value: Float): Color {
    val hsv = floatArrayOf(hue, saturation, value)
    val intColor = android.graphics.Color.HSVToColor(hsv)
    return Color(intColor)
}

fun DrawScope.drawColorWheel(radius: Float, strokeWidth: Float) {
    val center = Offset(size.width / 2, size.height / 2)
    for (i in 0 until 360) {
        val color = hsvToComposeColor(i.toFloat(), 1f, 1f)
        drawArc(
            color = color,
            startAngle = i.toFloat(),
            sweepAngle = 1f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
    }
}

fun hsvToComposeColor(hue: Float, saturation: Float, value: Float): ComposeColor {
    val hsv = floatArrayOf(hue, saturation, value)
    val intColor = AndroidColor.HSVToColor(hsv)  // <- 정확한 방법
    return ComposeColor(intColor)
}

@Preview
@Composable
fun SpeakerScreenPreview() {
    ColorWheelSelector(Modifier,{})
}