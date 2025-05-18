package com.hogumiwarts.lumos.ui.screens.control.airpurifier

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toCommandDeviceForAirPurifier
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
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
    val deviceId = selectedDevice.deviceId
    var selectedFanMode by remember { mutableStateOf("Auto") }
    var checked by remember { mutableStateOf(false) }
    var dustLevel by remember { mutableStateOf(0) }
    var fineDustLevel by remember { mutableStateOf(0) }
    var odorLevel by remember { mutableStateOf(0) }
    var filterUsageTime by remember { mutableStateOf(0) }
    var deviceModel by remember { mutableStateOf(" ") }
    var manufacturerCode by remember { mutableStateOf(" ") }
    var name by remember { mutableStateOf("공기청정기") }
    var airQualityText by remember { mutableStateOf("") }
    var airQualityColor by remember { mutableStateOf(Color.White) }

    val state by viewModel.state.collectAsState()
    val powerState by viewModel.powerState.collectAsState()

    LaunchedEffect(deviceId) {
        viewModel.sendIntent(AirpurifierIntent.LoadAirpurifierStatus(deviceId.toLong()))
    }


    when (state) {
        is AirpurifierStatusState.Loaded -> {
            LaunchedEffect(state) {
                val data = (state as AirpurifierStatusState.Loaded).data
                checked = data.activated
                selectedFanMode = data.fanMode
                dustLevel = data.dustLevel
                fineDustLevel = data.fineDustLevel
                odorLevel = data.odorLevel
                filterUsageTime = data.filterUsageTime
                deviceModel = data.deviceModel
                manufacturerCode = data.manufacturerCode
                name = data.deviceName

                airQualityText = when (data.caqi.toAirQuality()) {
                    AirQuality.VeryLow -> "매우 좋음"
                    AirQuality.Low -> "좋음"
                    AirQuality.Medium -> "보통"
                    AirQuality.High -> "나쁨"
                    AirQuality.VeryHigh -> "매우 나쁨"
                }

                airQualityColor = when (data.caqi.toAirQuality()) {
                    AirQuality.VeryLow -> Color(0xFF4CD137)
                    AirQuality.Low -> Color(0xFF7FBA00)
                    AirQuality.Medium -> Color(0xFFFBC531)
                    AirQuality.High -> Color(0xFFE84118)
                    AirQuality.VeryHigh -> Color(0xFFC23616)
                }
            }
        }

        else -> {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
                .padding(horizontal = 24.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                name,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = nanum_square_neo
            )
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("공기청정기", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        viewModel.sendIntent(AirpurifierIntent.ChangeAirpurifierPower(deviceId.toLong(), it))
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
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_airpur),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                "현재 공기 질",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = nanum_square_neo
            )
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xffC3C8E8), RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(15.dp)
                                .clip(CircleShape)
                                .background(airQualityColor)
                        )
                        Text(
                            " $airQualityText",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text("미세먼지 농도 $dustLevel μg/m³", fontSize = 14.sp, color = Color.Gray)
                    Text("초미세먼지 농도 $fineDustLevel μg/m³", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "팬 속도",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                fontFamily = nanum_square_neo
            )

            Spacer(Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF2F2F2),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .height(40.dp)
                    .padding(vertical = 7.dp, horizontal = 7.dp)
            ) {
                listOf("auto", "low", "medium", "high", "quiet").forEach { mode ->
                    FanButton(
                        mode,
                        selectedFanMode == mode,
                        { selectedFanMode = mode },
                        Modifier.weight(1f)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("기기 정보", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("필터 사용 시간 | ${filterUsageTime}시간", fontSize = 14.sp, color = Color.DarkGray)
            Text("모델명 | ${deviceModel}", fontSize = 14.sp, color = Color.DarkGray)
            Text("제조사 | ${manufacturerCode}", fontSize = 14.sp, color = Color.DarkGray)
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 27.dp)
            )
        }

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
                elevation = 10.dp,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000)
            )
            .background(
                color = if (isSelected) Color.White else Color.Transparent,
                RoundedCornerShape(6.dp)
            )
            .clickable { onClick() }
            .fillMaxHeight()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mode,
            color = Color.Black,
            fontFamily = nanum_square_neo,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

