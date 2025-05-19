package com.hogumiwarts.lumos.presentation.ui.screens.routine

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hogumiwarts.domain.model.gesture.GestureDetailData
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureDetailState
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureIntent
import com.hogumiwarts.lumos.presentation.ui.viewmodel.GestureViewModel


@Composable
fun RoutineExecuteScreen(
    gestureId: Long,
    gestureViewModel: GestureViewModel = hiltViewModel()
) {

    val state by gestureViewModel.state.collectAsState()
    var gestureImgUrl by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        when (state) {
            is GestureDetailState.Error -> {
                // todo 에러처리
            }

            GestureDetailState.Idle -> {}
            is GestureDetailState.Loaded -> {
                val data = (state as GestureDetailState.Loaded).data
                gestureImgUrl = data.gestureImageUrl
            }

            GestureDetailState.Loading -> {
                // todo 로딩처리
            }
        }
    }

    gestureViewModel.sendIntent(GestureIntent.LoadGestureDetail(gestureId))
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
            AsyncImage(
                model = gestureImgUrl,
                contentDescription = "제스처 이미지"
            )

            Log.d("TAG", "RoutineExecuteScreen: $gestureImgUrl")

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