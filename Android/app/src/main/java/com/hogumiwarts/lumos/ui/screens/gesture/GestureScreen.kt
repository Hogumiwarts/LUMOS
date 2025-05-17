package com.hogumiwarts.lumos.ui.screens.gesture

import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.hogumiwarts.lumos.R
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import timber.log.Timber
import com.hogumiwarts.lumos.ui.viewmodel.GestureViewModel


@Composable
fun GestureScreen(
    navController: NavController,
    onGestureSelected: (gestureId: Int) -> Unit, // 선택된 제스처 ID 콜백
    viewModel: GestureViewModel = hiltViewModel()
) {

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


        when (state) {
            is GestureState.Error -> {

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
