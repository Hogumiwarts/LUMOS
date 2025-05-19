package com.hogumiwarts.lumos.presentation.ui.screens.routine

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R

data class GestureDetailData(
    val gestureId: Long,
    val gestureName: String,
    val gestureImg: String,
    val gestureDescription: String
)

@Composable
fun RoutineExecuteScreen(
    gestureId: Long
) {

    val gesture = GestureDetailData(
        gestureId,
        "손목 회전",
        "https://example.com/image.png",
        "손목을 가볍게 회전합니다"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 배경
        Image(
            painter = painterResource(id = R.drawable.device_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 제스처 이미지
            // TODO: 이미지 url로 받기
//            AsyncImage(model = gesture.gestureImg,
//                contentDescription = "제스처 이미지")

            Image(
                painter = painterResource(id = R.drawable.ic_gesture),
                contentDescription = "테스트 제스처 이미지",
                modifier = Modifier.size(100.dp)
            )

            Text(
                "루틴이 실행되었습니다.",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

}


@Composable
@Preview(showBackground = true, device = Devices.WEAR_OS_SMALL_ROUND)
fun RoutineExecuteScreenPreview() {
    RoutineExecuteScreen(1L)
}