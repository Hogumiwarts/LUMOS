package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.airpurifier.AirpurifierData
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.common.ErrorMessage
import com.hogumiwarts.lumos.presentation.ui.common.OnOffSwitch
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.devices.components.LoadingDevice
import com.hogumiwarts.lumos.presentation.ui.viewmodel.AirpurifierViewModel

@Composable
fun AipurifierSwitch(
    deviceId: Long,
    navController: NavHostController,
    viewModel: AirpurifierViewModel = hiltViewModel()
) {
    val switchState = remember { mutableStateOf(false) }



    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.sendIntent(AirpurifierIntent.LoadAirpurifierStatus(deviceId))
    }
    // ViewModel 상태 수신
    val state by viewModel.state.collectAsState()


    when(state){
        is AirpurifierStatusState.Error -> {
            when ((state as AirpurifierStatusState.Error).error) {
                CommonError.NetworkError -> ErrorMessage("인터넷 연결을 확인해주세요.")
                CommonError.UserNotFound -> ErrorMessage("사용자를 찾을 수 없습니다.")
                else -> ErrorMessage("알 수 없는 오류가 발생했습니다.")
            }
        }
        AirpurifierStatusState.Idle -> {}
        is AirpurifierStatusState.Loaded -> {
            val data = (state as AirpurifierStatusState.Loaded).data
            leaded(navController, switchState,data,deviceId)
        }
        AirpurifierStatusState.Loading -> {
            LoadingDevice()
        }
    }


}

@Composable
private fun leaded(
    navController: NavHostController,
    switchState: MutableState<Boolean>,
    data: AirpurifierData,
    deviceId: Long,
    viewModel: AirpurifierViewModel = hiltViewModel()
) {

    val powerState by viewModel.powerState.collectAsState()
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.device_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        val (title, toggle, arrow) = createRefs()

        // 제목
        Text(
            text = data.deviceName,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 18.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toggle.top)
            }
        )

        // 볼륨 및 스위치 토글
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0x10FFFFFF))
                .clickable {
                    // 클릭 시 동작
                    navController.navigate("AipurifierSetting") {

                    }
                }
                .constrainAs(toggle) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    val fanModeText = data.fanMode.replaceFirstChar { it.uppercaseChar() }
                    Text(
                        text = "펜속도  $fanModeText",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    val caqiText = when (data.caqi) {
                        "VeryLow" -> "매우 좋음"
                        "Low" -> "좋음"
                        "Medium" -> "보통"
                        "High" -> "나쁨"
                        "VeryHigh" -> "매우 나쁨"
                        else -> "정보 없음"
                    }
                    Text(
                        text = "미세먼지 $caqiText",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }


                OnOffSwitch(
                    checked = switchState.value,
                    onCheckedChange = {
//                        switchState.value = it
                        viewModel.sendIntent(AirpurifierIntent.ChangeAirpurifierPower(deviceId = deviceId , activated = it ))
                        when(powerState){
                            is AirpurifierPowerState.Error -> {}
                            AirpurifierPowerState.Idle -> {}
                            is AirpurifierPowerState.Loaded -> {
                                switchState.value = it
                            }
                            AirpurifierPowerState.Loading -> {

                            }
                        }
//                        onToggle(it)
                    }
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier.constrainAs(arrow) {
                top.linkTo(toggle.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
        ) {
            Text(
                text = "폰에서 세부 제어",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = TextStyle(fontSize = 14.sp)
            )
        }
    }
    when(powerState){
        is AirpurifierPowerState.Error -> {}
        AirpurifierPowerState.Idle -> {}
        is AirpurifierPowerState.Loaded -> {
        }
        AirpurifierPowerState.Loading -> {
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