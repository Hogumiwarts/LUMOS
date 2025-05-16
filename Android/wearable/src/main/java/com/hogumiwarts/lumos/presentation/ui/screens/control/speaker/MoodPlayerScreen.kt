package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hogumiwarts.lumos.R
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.*
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.lumos.presentation.ui.common.ErrorMessage
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightIntent
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.devices.components.LoadingDevice
import com.hogumiwarts.lumos.presentation.ui.viewmodel.AudioViewModel


// 메인 화면 전환을 위한 상위 컴포저블
@Composable
fun MoodPlayerScreen(deviceId: Long, viewModel: AudioViewModel = hiltViewModel()) {




    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.sendIntent(AudioIntent.LoadAudioStatus(deviceId))
    }

//    val state by viewModel.state.collectAsState()

//    when(state){
//        is AudioStatusState.Error -> {
//            when ((state as AudioStatusState.Error).error) {
//                CommonError.NetworkError -> ErrorMessage("인터넷 연결을 확인해주세요.")
//                CommonError.UserNotFound -> ErrorMessage("사용자를 찾을 수 없습니다.")
//                else -> ErrorMessage("알 수 없는 오류가 발생했습니다.")
//            }
//        }
//        AudioStatusState.Idle -> {}
//        is AudioStatusState.Loaded -> {
//            LoadedScreen()
//        }
//        AudioStatusState.Loading -> {
//            LoadingDevice()
//        }
//    }
    LoadedScreen()

}

@Composable
private fun LoadedScreen() {
    var showNext by remember { mutableStateOf(false) } // 현재 화면/다음 화면 상태
    // 화면 전환 시 애니메이션
    val currentOffsetY by animateDpAsState(
        targetValue = if (showNext) (-300).dp else 0.dp,
        animationSpec = tween(400),
        label = "currentScreenOffset"
    )
    val nextOffsetY by animateDpAsState(
        targetValue = if (showNext) 0.dp else 300.dp,
        animationSpec = tween(400),
        label = "nextScreenOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.device_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        // 무드 플레이어 화면
        Box(modifier = Modifier.offset(y = currentOffsetY)) {
            MoodPlayerSwitch(
                volumePercent = 40,
                isOn = true,
                onToggle = {},
                onSwipeUp = { showNext = true }
            )
        }

        // 음악 플레이어 화면
        if (showNext || nextOffsetY < 300.dp) {
            Box(modifier = Modifier.offset(y = nextOffsetY)) {
                 MoodPlayerContainer(onSwipeDown = { showNext = false }) // 아래로 스와이프 시 복귀
            }
        }
    }
}


// ON/OFF 스위치 구현


