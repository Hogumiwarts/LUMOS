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
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toCommandDeviceForAirPurifierOff
import com.hogumiwarts.lumos.mapper.toCommandDeviceForAirPurifierOn
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun PreviewAirPurifierScreenContent(
    navController: NavController,
    selectedDevice: MyDevice,
) {
    val coroutineScope = rememberCoroutineScope()

    val deviceId = selectedDevice.deviceId
    var selectedFanMode by remember { mutableStateOf("auto") }
    var checked by remember { mutableStateOf(selectedDevice.isOn) }


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
                selectedDevice.deviceName,
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
                        checked = it
                        Timber.d("[DEBUG] 전원 상태 변경됨: $checked")
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

            if (checked) {
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
                            {
                                selectedFanMode = mode
                                Timber.d("[DEBUG] 팬 모드 선택됨: $selectedFanMode")
                            },
                            Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 하단 설정 버튼
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
                    val deviceWithCommands = if(selectedDevice.isOn){
                        selectedDevice.toCommandDeviceForAirPurifierOn(
                            isOn = checked,
                            fanMode = selectedFanMode
                        )
                    } else{
                        selectedDevice.toCommandDeviceForAirPurifierOff(
                            isOn = checked
                        )
                    }

                    val json = Gson().toJson(deviceWithCommands)

                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "commandDeviceJson",
                        json
                    )

                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "fanMode",
                        selectedFanMode
                    )

                    navController.currentBackStackEntry?.savedStateHandle?.set("isOn", checked)

                    coroutineScope.launch {
                        kotlinx.coroutines.delay(100)
                        navController.popBackStack()
                    }
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
