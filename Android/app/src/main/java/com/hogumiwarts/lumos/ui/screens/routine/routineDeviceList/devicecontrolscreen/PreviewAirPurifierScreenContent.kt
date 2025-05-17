package com.hogumiwarts.lumos.ui.screens.control.airpurifier

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toCommandDevice
import com.hogumiwarts.lumos.mapper.toCommandDeviceForAirPurifier
import com.hogumiwarts.lumos.ui.common.LoadingComponent
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.control.AirPurifierDevice
import com.hogumiwarts.lumos.ui.screens.control.AirQuality
import com.hogumiwarts.lumos.ui.screens.control.toAirQuality
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import com.hogumiwarts.lumos.ui.viewmodel.AirpurifierViewModel


@Composable
fun PreviewAirPurifierScreenContent(
    navController: NavController,
    selectedDevice: MyDevice,
    viewModel: AirpurifierViewModel = hiltViewModel()
) {
    var deviceId by remember { mutableStateOf(7L) }
    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {

        viewModel.sendIntent(AirpurifierIntent.LoadAirpurifierStatus(deviceId))
    }

    val state by viewModel.state.collectAsState()
    val powerState by viewModel.powerState.collectAsState()

    val airPurifier = AirPurifierDevice(
        tagNumber = 2,
        deviceId = 12345,
        deviceImg = "https://storage.googleapis.com/lumos-assets/devices/air_purifier.png",
        deviceName = "거실 공기청정기",
        manufacturerCode = "Samsung Electronics",
        deviceModel = "Samsung Air Purifier AX90T7080WD",
        deviceType = "공기청정기",
        activated = true,
        caqi = "VeryLow",  // 공기질
        odorLevel = 1,  // 현재 냄새 센서 수치
        dustLevel = 15,  // 미세먼지 농도
        fineDustLevel = 8,  // 초미세먼지 농도
        fanMode = "Auto",  // fan 속도
        filterUsageTime = 720  // 30일(720시간) 사용
    )


    // 공기 질 enum을 한국어 텍스트로 변환
    var airQualityText by remember { mutableStateOf("") }

    val airQualityEnum = airPurifier.caqi.toAirQuality()
    // 공기 질에 따른 색상 설정

    var airQualityColor by remember { mutableStateOf(Color.White) }


    var selectedFanMode by remember { mutableStateOf(airPurifier.fanMode) }
    var checked by remember { mutableStateOf(false) }
    var dustLevel by remember { mutableStateOf(0) }
    var fineDustLevel by remember { mutableStateOf(0) }
    var odorLevel by remember { mutableStateOf(0) }
    var filterUsageTime by remember { mutableStateOf(0) }
    var deviceModel by remember { mutableStateOf(" ") }
    var manufacturerCode by remember { mutableStateOf(" ") }
    var name by remember { mutableStateOf("공기청정기") }

    when (state) {
        is AirpurifierStatusState.Error -> {}
        AirpurifierStatusState.Idle -> {}
        is AirpurifierStatusState.Loaded -> {
            LaunchedEffect(state) {


                val data = (state as AirpurifierStatusState.Loaded).data
                checked = data.activated

                airQualityText = when (data.caqi.toAirQuality()) {
                    AirQuality.VeryLow -> "매우 좋음"
                    AirQuality.Low -> "좋음"
                    AirQuality.Medium -> "보통"
                    AirQuality.High -> "나쁨"
                    AirQuality.VeryHigh -> "매우 나쁨"
                }

                airQualityColor = when (data.caqi.toAirQuality()) {
                    AirQuality.VeryLow -> Color(0xFF4CD137)    // 밝은 초록색
                    AirQuality.Low -> Color(0xFF7FBA00)        // 초록색
                    AirQuality.Medium -> Color(0xFFFBC531)     // 노란색
                    AirQuality.High -> Color(0xFFE84118)       // 주황색
                    AirQuality.VeryHigh -> Color(0xFFC23616)   // 빨간색
                }

                dustLevel = data.dustLevel

                fineDustLevel = data.fineDustLevel

                odorLevel = data.odorLevel

                selectedFanMode = data.fanMode

                deviceModel = data.deviceModel

                manufacturerCode = data.manufacturerCode

                filterUsageTime = data.filterUsageTime

                name = data.deviceName

            }
        }

        AirpurifierStatusState.Loading -> {}
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
    ) {

        Spacer(modifier = Modifier.height(40.dp))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(41.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "공기청정기",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Switch(
                checked = checked,
                onCheckedChange = {
                    viewModel.sendIntent(AirpurifierIntent.ChangeAirpurifierPower(deviceId, it))
                    checked = it
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xff3E4784),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFB0B0B0)
                )
            )
        }

        Spacer(modifier = Modifier.height(17.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_airpur),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(17.dp))

        HorizontalDivider()
        Spacer(modifier = Modifier.height(17.dp))

        Text(
            "현재 공기 질",
            fontSize = 18.sp,
            fontFamily = nanum_square_neo,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(15.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, shape = RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = Color(0xffC3C8E8),
                    shape = RoundedCornerShape(12.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .clip(CircleShape)
                            .background(airQualityColor)
                    )

                    Text(
                        text = " $airQualityText",
                        fontSize = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 미세먼지 정보
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "\uD83E\uDE84  ",
                        color = Color.Gray,
                        fontFamily = nanum_square_neo,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "미세먼지 농도 ${dustLevel} μg/m³",
                        color = Color.Gray,
                        fontFamily = nanum_square_neo,
                        fontSize = 14.sp

                    )
                }

                // 초미세먼지 정보
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Text(
                        text = "\uD83E\uDE84  ",
                        color = Color.Gray,
                        fontFamily = nanum_square_neo,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "초미세먼지 농도 ${fineDustLevel} μg/m³",
                        color = Color.Gray,
                        fontFamily = nanum_square_neo,
                        fontSize = 14.sp
                    )
                }

            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .border(
                        1.dp, color = Color(0xffC3C8E8),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("미세먼지 $airQualityText", fontFamily = nanum_square_neo,)
            }

            Box(
                modifier = Modifier
                    .border(
                        1.dp, color = Color(0xffC3C8E8),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("냄새 ${mapNumberToText(odorLevel)}", fontFamily = nanum_square_neo,)
            }
        }

        Spacer(modifier = Modifier.height(17.dp))

        Text(
            "팬 속도",
            fontSize = 18.sp,
            fontFamily = nanum_square_neo,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 팬 속도 옵션 배열
            val fanModes = listOf("auto", "low", "medium", "high", "quiet")

            // 각 팬 속도 옵션에 대한 버튼 생성
            fanModes.forEach { mode ->
                FanButton(
                    mode = mode,
                    isSelected = selectedFanMode == mode,
                    onClick = {
                        selectedFanMode = mode
                        Log.d("TAG", "AirpurifierScreen: $selectedFanMode  $mode")
                        viewModel.sendIntent(
                            AirpurifierIntent.ChangeAirpurifierFenMode(
                                deviceId,
                                selectedFanMode
                            )
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }


        Spacer(modifier = Modifier.height(17.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(17.dp))

        // 기기 정보
        Text(
            text = "기기 정보",
            fontWeight = FontWeight.Bold,
            fontFamily = nanum_square_neo,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "필터 사용 시간 | ${filterUsageTime}시간",
            fontSize = 14.sp,
            fontFamily = nanum_square_neo,
            color = Color.DarkGray
        )

        Text(
            text = "모델명 | ${deviceModel}",
            fontSize = 14.sp,
            fontFamily = nanum_square_neo,
            color = Color.DarkGray
        )

        Text(
            text = "제조사 | ${manufacturerCode}",
            fontSize = 14.sp,
            fontFamily = nanum_square_neo,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(150.dp))

        // 하단 고정 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            PrimaryButton(
                buttonText = "설정하기",
                onClick = {
                    val deviceWithCommands = selectedDevice.toCommandDeviceForAirPurifier(
                        isOn = checked,
                        fanMode = selectedFanMode
                    )

                    val json = Gson().toJson(deviceWithCommands)
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "commandDeviceJson",
                        json
                    )
                    navController.popBackStack()

                },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }

    when (powerState) {
        is AirpurifierPowerState.Error -> {}
        AirpurifierPowerState.Idle -> {}
        is AirpurifierPowerState.Loaded -> {

            checked = (powerState as AirpurifierPowerState.Loaded).data.activated
            Log.d("TAG", "AirpurifierScreen: $checked")

        }

        AirpurifierPowerState.Loading -> {
            LoadingComponent()
        }
    }

}

fun mapNumberToText(value: Int): String {
    return when (value) {
        1 -> "매우 좋음"
        2 -> "좋음"
        3 -> "보통"
        4 -> "안좋음"
        5 -> "매우 안좋음"
        else -> "알 수 없음"
    }
}


@Composable
fun FanButton(
    mode: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = if (isSelected) 4.dp else 0.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                Color.White,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mode,
            color = Color.Black,
            fontFamily = nanum_square_neo,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
g