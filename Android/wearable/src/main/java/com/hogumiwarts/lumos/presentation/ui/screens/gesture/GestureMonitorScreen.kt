package com.hogumiwarts.lumos.presentation.ui.screens.gesture

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hogumiwarts.lumos.presentation.ui.viewmodel.WebSocketViewModel

@Composable
fun GestureMonitorScreen(
    isMonitoring: Boolean,
    onToggleMonitoring: () -> Unit,
    onStop: () -> Unit
) {

    val context = LocalContext.current
    val webSocketViewModel: WebSocketViewModel = viewModel()
    val prediction by webSocketViewModel.prediction

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1021)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 상태 표시
            Text(
                text = if (isMonitoring) "제스처 감지 중..." else "감지 중지됨",
                fontSize = 14.sp,
                color = if (isMonitoring) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.size(12.dp))

            // 마지막 예측
            Text(
                text = "예측: $prediction",
                fontSize = 12.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.size(20.dp))

            // 감지 시작/중지 버튼
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isMonitoring) Color(0xFFFF4444) else Color(0xFF4CAF50))
                    .clickable { onToggleMonitoring() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (isMonitoring) "감지 중지" else "감지 시작",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.size(16.dp))

            // 종료 버튼
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF3A3A3C))
                    .clickable { onStop() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "종료",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }

}