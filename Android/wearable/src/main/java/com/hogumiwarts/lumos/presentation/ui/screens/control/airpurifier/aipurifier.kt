package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

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
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchIntent
//import com.hogumiwarts.lumos.presentation.ui.viewmodel.AirpurifierViewModel


// 메인 화면 전환을 위한 상위 컴포저블
@Composable
fun Aipurifier(

) {
    var showNext by remember { mutableStateOf(false) } // 현재 화면/다음 화면 상태


    val nextOffsetY by animateDpAsState(
        targetValue = if (showNext) 0.dp else 300.dp,
        animationSpec = tween(400),
        label = "nextScreenOffset"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // 무드 플레이어 화면
        Box(modifier = Modifier.offset()) {
//            AipurifierSwitch(
//                volumePercent = 40,
//                isOn = true,
//                onSwipeUp = { showNext = true }
//            )
        }

        // 음악 플레이어 화면
        if (showNext || nextOffsetY < 300.dp) {
            Box(modifier = Modifier.offset(x = nextOffsetY)) {
//                AipurifierSetting(onSwipeDown = { showNext = false })
            }
        }
    }
}




// 미리보기 (NextScreen 기준)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun AirPreview() {
    Aipurifier()
}

