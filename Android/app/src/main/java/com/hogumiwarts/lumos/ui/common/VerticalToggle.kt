package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun VerticalToggle(
    isOn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 토글 배경
    Box(
        modifier = modifier
            .width(30.dp)
            .height(53.dp)
            .clip(RoundedCornerShape(50))
            .background(if (isOn) Color(0xFFA9AFD9) else Color(0xFFE2E2E2))
            .clickable { onToggle() }
            .padding(4.dp),
        contentAlignment = if (isOn) Alignment.TopCenter else Alignment.BottomCenter
    ) {
        // 토글 내부 원
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        )
    }
}
