package com.hogumiwarts.lumos.presentation.ui.screens.gesture

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme

@Composable
fun GestureTestScreen(
    onFinish: () -> Unit
) {
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
            // 상태 텍스트
            Text(
                text = "●  인식 중..",
                fontSize = 11.sp,
                color = Color.LightGray
            )
            Spacer(modifier = Modifier.size(8.dp))
            // 안내 문구
            Text(
                text = "제스처를 실행해보세요.",
                fontSize = 15.sp,
                color = Color.White
            )

            // 제스처 이모지
            Image(
                painter = painterResource(id = R.drawable.ic_motion1),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
            )
            // 완료 버튼
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF3A3A3C))
                    .clickable { onFinish() }
                    .padding(horizontal = 33.dp, vertical = 11.dp)
            ) {
                Text(
                    text = "완료하기",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme{
        GestureTestScreen({})
    }
}