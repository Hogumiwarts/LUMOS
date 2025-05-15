package com.hogumiwarts.lumos.ui.screens.routine.components

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GlowingCard(
    modifier: Modifier = Modifier,
    glowingColor: Color = Color(0xFF3D5AFE),
    containerColor: Color = Color.White,
    cornerRadius: Dp = 10.dp,
    glowingRadius: Dp = 10.dp,
    xOffset: Dp = 0.dp,
    yOffset: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .drawBehind {
                val inset = glowingRadius.toPx() / 5f // 이 값을 줄이면 glow 범위가 줄어듦

                val paint = Paint().apply {
                    color = containerColor.toArgb()
                    setShadowLayer(
                        glowingRadius.toPx(),
                        xOffset.toPx(),
                        yOffset.toPx(),
                        glowingColor.toArgb()
                    )
                }

                drawContext.canvas.nativeCanvas.apply {
                    drawRoundRect(
                        inset,
                        inset,
                        size.width - inset,
                        size.height - inset,
                        cornerRadius.toPx(),
                        cornerRadius.toPx(),
                        paint
                    )
                }
            }
            .clip(shape)
            .background(containerColor, shape)
    ) {
        content()
    }
}
