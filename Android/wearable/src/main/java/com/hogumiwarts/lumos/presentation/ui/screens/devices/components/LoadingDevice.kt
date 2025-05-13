package com.hogumiwarts.lumos.presentation.ui.screens.devices.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hogumiwarts.lumos.R


@Composable
fun LoadingDevice() {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .fillMaxSize(),
        contentAlignment = Alignment.Center // 중앙 정렬
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.device_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )

        // 로딩 인디케이터
        CircularProgressIndicator(
            color = Color.White, // 원하면 다른 색상으로 변경 가능
            strokeWidth = 3.dp,
            modifier = Modifier.size(32.dp)
        )
    }
}