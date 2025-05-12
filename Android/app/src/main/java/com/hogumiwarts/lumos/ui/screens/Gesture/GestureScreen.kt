package com.hogumiwarts.lumos.ui.screens.Gesture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.wearable.Wearable
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.lumos.GestureTestViewModel
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginViewModel
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme
import kotlinx.coroutines.flow.collectLatest


@Composable
fun GestureScreen(viewModel: GestureViewModel = hiltViewModel()) {


    LaunchedEffect(Unit) {
        viewModel.channel.send(GestureIntent.LoadGesture)
    }


    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()



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
