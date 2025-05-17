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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.lumos.ui.screens.routine.routineCreate.RoutineCreateViewModel
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme
import timber.log.Timber
import com.hogumiwarts.lumos.ui.viewmodel.GestureViewModel


@Composable
fun GestureScreen(
    navController: NavController,
    onGestureSelected: (gestureId: Int) -> Unit, // 선택된 제스처 ID 콜백
    viewModel: GestureViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.intent.emit(GestureIntent.LoadGesture)
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


        when (state) {
            is GestureState.Error -> {
                // todo 에러 처리
            }

            GestureState.Idle -> {}
            is GestureState.LoadedGesture -> {
                val gestures = (state as GestureState.LoadedGesture).data
                Timber.tag("gesture").d("✅ 받아온 제스처 수: ${gestures.size}")
                gestures.forEach {
                    Timber.tag("gesture").d("📦 ${it.gestureId} / ${it.gestureName} / ${it.gestureImageUrl}")
                }

                GestureTest(
                    cards = gestures,
                    onGestureSelected = { gestureData ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedGesture", gestureData)
                        navController.popBackStack()
                    }
                )
            }


            GestureState.Loading -> {
                // todo 로딩 처리
            }
        }


    }


}


//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    LUMOSTheme {
//        Surface(
//            modifier = Modifier.fillMaxSize(),
//            color = Color.Transparent
//        ) {
//            GestureScreen()
//        }
//    }
//}
