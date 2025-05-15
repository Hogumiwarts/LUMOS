package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.viewmodel.AirpurifierViewModel

@Composable
fun AipurifierSetting(deviceId: Long,type: String, viewModel: AirpurifierViewModel = hiltViewModel()) {

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()

    ) {

        val options = listOf("Auto","Low", "Medium", "High","Quiet") // auto: 자동 / low: 약풍 / medium: 중간풍 / high: 강풍 / quiet: 조용한 모드
        // 전달받은 type을 대소문자 무시하고 비교하여 인덱스 찾기

        val initialIndex = options.indexOfFirst { it.equals(type, ignoreCase = true) }.coerceAtLeast(0)

        val selectedIndex = remember { mutableStateOf(initialIndex) }

        Image(
            painter = painterResource(id = R.drawable.device_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )

        val (title, player, more) = createRefs()
        // 노래 제목 및 아티스트

        Column(
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(player.top)
            },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "팬속도", fontSize = 16.sp, color = Color.White)
        }

        // 볼륨 및 스위치 토글
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(30.dp))
                .constrainAs(player) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            FanSpeedSelector(selectedIndex)
        }
        // 하단 안내 텍스트
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier.constrainAs(more) {
                top.linkTo(player.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Text(
                text = "적용하기",
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .clickable {
                        viewModel.sendIntent(
                            AirpurifierIntent.ChangeAirpurifierFenMode(
                                deviceId = deviceId,
                                fanMode = options[selectedIndex.value].lowercase()
                            )
                        )
                        Log.d(
                            "TAG",
                            "AipurifierSetting: ${options[selectedIndex.value].lowercase()}"
                        )
                    },
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }

    val fanModeState by viewModel.fanModeState.collectAsState()
    when(fanModeState){
        is AirpurifierFanModeState.Error -> {}
        AirpurifierFanModeState.Idle -> {}
        is AirpurifierFanModeState.Loaded -> {

        }
        AirpurifierFanModeState.Loading -> {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


@Composable
fun FanSpeedSelector(selectedIndex: MutableState<Int>) {

    val options = listOf("Auto","Low", "Medium", "High","Quiet") // auto: 자동 / low: 약풍 / medium: 중간풍 / high: 강풍 / quiet: 조용한 모드
    // 전달받은 type을 대소문자 무시하고 비교하여 인덱스 찾기


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (totalDrag < -50f) {
                            selectedIndex.value = (selectedIndex.value + 1) % options.size
                        } else if (totalDrag > 50f) {
                            selectedIndex.value =
                                (selectedIndex.value - 1 + options.size) % options.size
                        }
                        totalDrag = 0f
                    },
                    onHorizontalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 왼쪽
            Text(
                text = options[(selectedIndex.value - 1 + options.size) % options.size],
                fontSize = 18.sp,
                color = Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )

            // 가운데
            Text(
                text = options[selectedIndex.value],
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
            )

            // 오른쪽
            Text(
                text = options[(selectedIndex.value + 1) % options.size],
                fontSize = 18.sp,
                color = Color.Gray.copy(alpha = 0.4f),
                modifier = Modifier.weight(0.5f),
                textAlign = TextAlign.Center
            )
        }
    }
}