package com.hogumiwarts.lumos.ui.screens.gesture

import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.hogumiwarts.lumos.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme


@Composable
fun GestureScreen(viewModel: GestureViewModel = hiltViewModel()) {


    LaunchedEffect(Unit) {
        viewModel.channel.send(GestureIntent.LoadGesture)
    }


    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_gesture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val dummyGestureData = listOf(
            GestureData(
                memberGestureId = 1L,
                gestureName = "주먹 쥠",
                description = "주먹을 꽉 쥐는 동작입니다.",
                gestureImg = "https://example.com/images/fist.png",
                routineName = "조명 켜기"
            ),
            GestureData(
                memberGestureId = 2L,
                gestureName = "손 펴기",
                description = "손을 완전히 펴는 동작입니다.",
                gestureImg = "https://example.com/images/open_hand.png",
                routineName = ""
            ),
            GestureData(
                memberGestureId = 3L,
                gestureName = "손목 회전",
                description = "손목을 시계 방향으로 회전합니다.",
                gestureImg = "https://example.com/images/wrist_rotate.png",
                routineName = ""
            )
        )
        GestureTest(dummyGestureData)
//        when (state) {
//            is GestureState.Idle -> {
//                // 아무 것도 안함 (초기 상태)
//            }
//
//            is GestureState.Loading -> {
//                // 🔄 로딩 UI 표시
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center),
//                    color = Color.White
//                )
//
//            }
//
//            is GestureState.LoadedGesture -> {
//                when (val data = (state as GestureState.LoadedGesture).data) {
//                    GestureResult.InvalidPassword -> {}
//                    GestureResult.NetworkError -> {}
//                    is GestureResult.Success -> {
//                        GestureTest(data.data)
//                    }
//
//                    GestureResult.UnknownError -> {}
//                    GestureResult.UserNotFound -> {}
//                }
//
//            }
//
//        }

    }


}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            GestureScreen()
        }
    }
}
