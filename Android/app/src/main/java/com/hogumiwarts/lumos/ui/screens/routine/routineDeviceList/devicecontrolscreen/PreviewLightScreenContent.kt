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
import com.hogumiwarts.lumos.mapper.toCommandDevice
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.control.components.GradientColorSlider
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.LightPreviewViewModel

@Composable
fun PreviewLightScreenContent(
    viewModel: LightPreviewViewModel,
    selectedDevice: MyDevice,
    navController: NavController
) {
    LaunchedEffect(Unit) {
        viewModel.setInitialState(
            isOn = selectedDevice.isOn,
            brightness = 50,
            hue = 180f,
            saturation = 100f
        )
    }

    val isOn by viewModel.isOn.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val hue by viewModel.hue.collectAsState()
    val saturation by viewModel.saturation.collectAsState()

    val controller = rememberColorPickerController()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

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
                    onCheckedChange = { viewModel.setPower(it) },
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

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "밝기",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Slider(
                value = brightness.toFloat(),
                onValueChange = { viewModel.setBrightness(it.toInt()) },
                valueRange = 0f..100f,
                steps = 0,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xff3E4784),
                    activeTrackColor = Color(0xff3E4784),
                    inactiveTrackColor = Color(0xffB9C0D4)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("0", fontSize = 12.sp, color = Color.Gray)
                Text("100", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "색 온도",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.align(Alignment.Start)
            )

            GradientColorSlider(modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("2200K", fontSize = 12.sp, color = Color.Gray)
                Text("6500K", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("색상 설정", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)

            HsvColorPicker(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
                    .padding(10.dp),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    val hsv = FloatArray(3)
                    android.graphics.Color.colorToHSV(colorEnvelope.color.toArgb(), hsv)
                    viewModel.setColor(hsv[0], hsv[1] * 100)
                }
            )

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

        // 하단 고정 버튼
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            PrimaryButton(
                buttonText = "설정하기",
                onClick = {
                    val deviceWithCommands = selectedDevice.toCommandDevice(
                        isOn = isOn,
                        brightness = brightness,
                        hue = hue,
                        saturation = saturation
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
}
