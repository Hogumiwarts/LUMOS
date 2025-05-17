package com.hogumiwarts.lumos.ui.screens.Gesture

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
import com.hogumiwarts.lumos.ui.viewmodel.GestureViewModel


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


        when(state){
            is GestureState.Error -> {
                // todo 에러 처리
            }
            GestureState.Idle -> {}
            is GestureState.LoadedGesture -> {
                GestureTest((state as GestureState.LoadedGesture).data)
            }
            GestureState.Loading -> {
                // todo 로딩 처리
            }
        }

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
