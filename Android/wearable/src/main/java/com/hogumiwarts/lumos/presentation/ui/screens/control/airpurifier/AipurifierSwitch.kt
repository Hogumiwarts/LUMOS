package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.hogumiwarts.lumos.presentation.ui.common.AnimatedMobile
import com.hogumiwarts.lumos.presentation.ui.common.ErrorMessage
import com.hogumiwarts.lumos.presentation.ui.common.OnOffSwitch
import com.hogumiwarts.lumos.presentation.ui.function.sendOpenLightMessage
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchStatusState
import com.hogumiwarts.lumos.presentation.ui.screens.devices.components.LoadingDevice
import com.hogumiwarts.lumos.presentation.ui.viewmodel.AirpurifierViewModel
import com.hogumiwarts.lumos.presentation.ui.viewmodel.DeviceViewModel
import kotlinx.coroutines.delay

@Composable
fun AipurifierSwitch(
    deviceId: Long,
    navController: NavHostController,
    viewModel: AirpurifierViewModel = hiltViewModel()
) {
    val switchState = remember { mutableStateOf(true) }



    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.sendIntent(AirpurifierIntent.LoadAirpurifierStatus(deviceId))
    }

    // ViewModel 상태 수신
    val state by viewModel.state.collectAsState()
    val isOn by viewModel.isOn.collectAsState()

    when(state){
        is AirpurifierStatusState.Error -> {
            when ((state as AirpurifierStatusState.Error).error) {
                CommonError.NetworkError -> ErrorMessage("인터넷 연결을 확인해주세요.")
                CommonError.UserNotFound -> ErrorMessage("사용자를 찾을 수 없습니다.")
                CommonError.UnauthorizedAccess->{
                    navController.navigate("login") {
                        // 뒤로가기 시 로그인 화면으로 못 돌아가게 스택 클리어 옵션 추가 가능
                        popUpTo("home") { inclusive = true } // 필요에 따라 홈 등 이전 화면 지정
                        launchSingleTop = true
                    }
                }
                else -> ErrorMessage("알 수 없는 오류가 발생했습니다.")
            }
        }
        AirpurifierStatusState.Idle -> {}
        is AirpurifierStatusState.Loaded -> {
            val data = (state as AirpurifierStatusState.Loaded).data
            switchState.value=data.activated
            leaded(navController, isOn,data,deviceId)

        }
        AirpurifierStatusState.Loading -> {
            LoadingDevice()
        }
    }


}

@Composable
private fun leaded(
    navController: NavHostController,
    switchState: Boolean,
    data: AirpurifierData,
    deviceId: Long,
    viewModel: AirpurifierViewModel = hiltViewModel(),
//    viewModel1: DeviceViewModel = hiltViewModel()
) {

    val isOn by viewModel.isOn.collectAsState()
    val powerState by viewModel.powerState.collectAsState()
//    viewModel1.saveJwt("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MDM1MDI0LCJleHAiOjE3NDcxMjE0MjR9.fZSp8dEpCWN-k1bB2zF_IEVn1Yi7_lIeev_zTJERnqY","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3Mjg5MDAzLCJleHAiOjE3NDc4OTM4MDN9.XLnwDciZxOjolAJfpM1Ej7a_UNB9-kRphbvZL5RIOHo")

    // 폰에서 세부 설정 클릭시 애니메이션 효과 여부
    var showAnimation by remember { mutableStateOf(false) }
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
                    navController.navigate("AipurifierSetting/${data.fanMode}/${deviceId}") {

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
                    checked = isOn,
                    onCheckedChange = {
//                        switchState.value = it
                        viewModel.sendIntent(AirpurifierIntent.ChangeAirpurifierPower(deviceId = deviceId , activated = it ))

//                        onToggle(it)
                    }
                )
            }
        }
        val context = LocalContext.current
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier
                .constrainAs(arrow) {
                    top.linkTo(toggle.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    showAnimation = true
                    sendOpenLightMessage(context, deviceId = deviceId, deviceType = "AIRPURIFIER")
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
    Box(modifier = Modifier.fillMaxSize()){
        AnimatedVisibility(
            visible = showAnimation,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AnimatedMobile()
        }
    }
    // ✅ 2초 후 자동으로 사라지기
    LaunchedEffect(showAnimation) {
        if (showAnimation) {
            delay(2000)
            showAnimation = false
        }
    }

    when(powerState){
        is AirpurifierPowerState.Error -> {
            when ((powerState as AirpurifierStatusState.Error).error) {
                CommonError.NetworkError -> ErrorMessage("인터넷 연결을 확인해주세요.")
                CommonError.UserNotFound -> ErrorMessage("사용자를 찾을 수 없습니다.")
                CommonError.UnauthorizedAccess->{
                    navController.navigate("login") {
                        // 뒤로가기 시 로그인 화면으로 못 돌아가게 스택 클리어 옵션 추가 가능
                        popUpTo("home") { inclusive = true } // 필요에 따라 홈 등 이전 화면 지정
                        launchSingleTop = true
                    }
                }
                else -> ErrorMessage("알 수 없는 오류가 발생했습니다.")
            }
        }
        AirpurifierPowerState.Idle -> {}
        is AirpurifierPowerState.Loaded -> {}
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