package com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.devicecontrolscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toCommandDevice
import com.hogumiwarts.lumos.mapper.toCommandDeviceForSwitch
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.control.SwitchDevice
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun PreviewSwitchScreenContent(
    navController: NavController,
    selectedDevice: MyDevice
) {
    var isChecked by remember { mutableStateOf(selectedDevice.isOn) }

    // todo: 더미 데이터 api 연동....!
    val switchDevice = remember {
        SwitchDevice(
            tagNumber = 1,
            deviceId = 123,
            manufactureCode = "MiniBig",
            deviceImg = "https://example.com/test.png",
            deviceName = "침대 조명 스위치",
            deviceType = "스위치",
            deviceModel = "Smart Switch v2",
            activated = true
        )
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
                .padding(bottom = 100.dp) // 버튼 공간 확보
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = selectedDevice.deviceName,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontFamily = nanum_square_neo,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "스위치",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    fontFamily = nanum_square_neo
                )
                Switch(
                    checked = isChecked,
                    onCheckedChange = { isChecked = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xff3E4784),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFB0B0B0)
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_switch),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(54.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {

                Text(
                    "기기 정보",
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = nanum_square_neo,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(13.dp))

                Text(
                    "제조사 | ${switchDevice.manufactureCode}",
                    fontSize = 12.sp,
                    fontFamily = nanum_square_neo,
                )

                Text(
                    "모델명 | ${switchDevice.deviceModel}",
                    fontSize = 12.sp,
                    fontFamily = nanum_square_neo,
                )
                Text(
                    "연결방식 | Wi-Fi",
                    fontSize = 12.sp,
                    fontFamily = nanum_square_neo,
                )
                Text(
                    "기기 타입 | ${switchDevice.deviceType}",
                    fontSize = 12.sp,
                    fontFamily = nanum_square_neo,
                )

            }

        }

        // 하단 고정 버튼
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
                    val commandDevice = selectedDevice.toCommandDeviceForSwitch(isOn = isChecked)
                    val json = Gson().toJson(commandDevice)
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
}
