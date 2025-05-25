package com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.devicecontrolscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.gson.Gson
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toCommandDeviceForLightOff
import com.hogumiwarts.lumos.mapper.toCommandDeviceForLightOn
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.control.components.GradientColorSlider
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.LightPreviewViewModel

@Composable
fun PreviewLightScreenContent(
    selectedDevice: MyDevice,
    navController: NavController
) {
    // 로컬 상태만 사용 (실시간 API 연동 제거)
    var isOn by remember { mutableStateOf(selectedDevice.isOn) }
    var brightness by remember { mutableStateOf(50) }
    var hue by remember { mutableStateOf(180f) }
    var saturation by remember { mutableStateOf(100f) }

    val controller = rememberColorPickerController()

    LaunchedEffect(Unit) {
        val currentEntry = navController.currentBackStackEntry
        val previousEntry = navController.previousBackStackEntry
        println("🔍 PreviewLightScreen의 현재 백스택: $currentEntry")
        println("🔍 PreviewLightScreen의 이전 백스택: $previousEntry")
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
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = selectedDevice.deviceName,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("조명", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Switch(
                    checked = isOn,
                    onCheckedChange = {
                        isOn = it
                    },
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
                painter = painterResource(id = R.drawable.ic_light),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )

            if (isOn) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "밝기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.align(Alignment.Start)
                )

                Slider(
                    value = brightness.toFloat(),
                    onValueChange = {
                        brightness = it.toInt()
                        println("[DEBUG] 밝기 변경: $brightness")
                    },
                    valueRange = 0f..100f,
                    steps = 0,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xff3E4784),
                        activeTrackColor = Color(0xff3E4784),
                        inactiveTrackColor = Color(0xffB9C0D4)
                    )
                )

                // 색온도
                Spacer(modifier = Modifier.height(24.dp))
                Text("색 온도", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                GradientColorSlider(
                    deviceId= 0,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                    Text("2200K", fontSize = 12.sp, color = Color.Gray)
                    Text("6500K", fontSize = 12.sp, color = Color.Gray)
                }

                // 색상 선택
                Spacer(modifier = Modifier.height(24.dp))
                Text("색상 설정", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

                HsvColorPicker(
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp)
                        .padding(10.dp),
                    controller = controller,
                    onColorChanged = { colorEnvelope ->
                        val hsv = FloatArray(3)
                        android.graphics.Color.colorToHSV(colorEnvelope.color.toArgb(), hsv)
                        hue = hsv[0]
                        saturation = hsv[1] * 100
                        println("[DEBUG] 색상 변경: hue=$hue, saturation=$saturation")
                    }
                )

                // 미리보기 박스
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val colorInt =
                        android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation / 100, 1f))
                    val composeColor = Color(colorInt)

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(composeColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "#%02X%02X%02X".format(
                            (colorInt shr 16) and 0xFF,
                            (colorInt shr 8) and 0xFF,
                            colorInt and 0xFF
                        ),
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }


        }

        // 설정 버튼: 루틴용 커맨드로 변환하여 되돌아가기
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            PrimaryButton(
                buttonText = "설정하기",
                onClick = {
                    val deviceWithCommands = if (!isOn) {
                        selectedDevice.toCommandDeviceForLightOff(isOn = false)
                    } else {
                        selectedDevice.toCommandDeviceForLightOn(
                            isOn = true,
                            brightness = brightness,
                            hue = hue * 10 / 36,
                            saturation = saturation
                        )
                    }

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
