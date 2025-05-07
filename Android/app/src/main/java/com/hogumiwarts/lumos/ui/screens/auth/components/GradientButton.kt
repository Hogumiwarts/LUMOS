package com.hogumiwarts.lumos.ui.screens.auth.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun GradientButton(onClick: () -> Unit, inputText: String = "시작하기") {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(
                color = Color(0xFF020014),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFE3E3E3), Color(0xFF231F59))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = inputText,
            style = MaterialTheme.typography.titleSmall.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight(700),
                fontFamily = nanum_square_neo,
                color = Color.White
            )
        )

        // 배경 원 그라데이션 효과
        Image(
            painter = painterResource(id = R.drawable.bg_gradient_button),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .clip(RoundedCornerShape(16.dp))
        )
    }
}


@Composable
@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    name = "GradientButton Preview",
    widthDp = 360,
    heightDp = 100
)
fun GradientButtonPreview() {
    GradientButton(
        onClick = {},
        inputText = "시작하기"
    )
}
