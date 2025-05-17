package com.hogumiwarts.lumos.ui.screens.control.light

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.hogumiwarts.domain.model.light.LightStatusData
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.LoadingComponent
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.control.components.GradientColorSlider
import com.hogumiwarts.lumos.ui.viewmodel.LightViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RealLightScreenContent(
    viewModel: LightViewModel,
    selectedDevice: MyDevice
) {
    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.sendIntent(LightIntent.LoadLightStatus(selectedDevice.deviceId.toInt()))
    }

    val state by viewModel.state.collectAsState()
    val powerState by viewModel.powerState.collectAsState()
    val brightnessState by viewModel.brightnessState.collectAsState()
    val colorState by viewModel.colorState.collectAsState()
    val temperatureState by viewModel.temperatureState.collectAsState()

    var isColor by remember { mutableStateOf(false) }

    var checked by remember { mutableStateOf(false) }
    var lightDevice by remember { mutableStateOf<LightStatusData?>(null) }
    var brightness by remember { mutableIntStateOf(0) }
    var selectedColorCode by remember { mutableStateOf("#FFFFFF") }
    var selectedColor by remember { mutableStateOf(Color.White) }

    val controller = rememberColorPickerController()

    when (state) {
        is LightStatusState.Error -> {}
        LightStatusState.Idle -> {}
        is LightStatusState.Loaded -> {
            LaunchedEffect(state) {
                val data = (state as LightStatusState.Loaded).data
                checked = data.activated
                brightness = data.brightness
                controller.selectByHsv(
                    (data.hue * 36 / 10).toFloat(),
                    data.saturation / 100,
                    1f,
                    1f,
                    false
                )
                lightDevice = data
            }
        }

        LightStatusState.Loading -> {}
    }

    Column(
    modifier = Modifier
    .fillMaxSize()
    .background(Color.White)
    .verticalScroll(rememberScrollState())
    .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Spacer(modifier = Modifier.height(40.dp))
        Text(
            text = lightDevice?.deviceName ?: "",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(41.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "조명",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Switch(
                checked = checked,
                onCheckedChange = {
                    viewModel.sendIntent(LightIntent.ChangeLightPower(selectedDevice.deviceId.toInt(), it))
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xff3E4784),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFB0B0B0)
                )
            )
        }

        Image(
            painter = painterResource(id = R.drawable.ic_light),
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(17.dp))

        // 밝기
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "밝기",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Slider(
                value = brightness.toFloat(),
                onValueChange = {
                    brightness = it.toInt()
                },
                onValueChangeFinished = {
                    viewModel.sendIntent(LightIntent.ChangeLightBright(9, brightness))
                },
                valueRange = 0f..100f,
                steps = 0,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xff3E4784),
                    activeTrackColor = Color(0xff3E4784),
                    inactiveTrackColor = Color(0xffB9C0D4)
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "0",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "100",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(17.dp))
        // 색온도
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "색 온도",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            GradientColorSlider(
                modifier = Modifier
                    .weight(1f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "2200K",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "6500K",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(41.dp))
        // 색상 설정
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "색상 설정",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            val coroutineScope = rememberCoroutineScope()
            var debounceJob by remember { mutableStateOf<Job?>(null) }

            HsvColorPicker(
                modifier = Modifier
                    .height(200.dp)
                    .width(200.dp)
                    .padding(10.dp),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    selectedColorCode = "#" + colorEnvelope.hexCode.substring(2)
                    selectedColor = colorEnvelope.color

                    // Color -> HSV 변환
                    val hsv = FloatArray(3)
//                    ColorUtils.colorToHSL(colorEnvelope.color.toArgb(), hsv)
                    android.graphics.Color.colorToHSV(colorEnvelope.color.toArgb(),hsv)


                    val hue = hsv[0]
                    val saturation = hsv[1]
                    val saturation1 = hsv[2]

                    if(isColor == true){

                    } else{
                        debounceJob?.cancel()
                        debounceJob = coroutineScope.launch {
                            delay(300)
                            Log.d("ColorPicker", "Hue: $hue, Saturation: $saturation, saturation1: $saturation1")
                            Log.d("ColorPicker", "Hue: $selectedColorCode")
                            viewModel.sendIntent(
                                LightIntent.ChangeLightColor(
                                    9,
                                    (hue * 10 / 36),
                                    saturation * 100
                                )
                            )
                        }
                    }

                    isColor = false
                }
            )
        }

        Row() {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(selectedColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                selectedColorCode,
                fontSize = 18.sp
            )
        }


        Spacer(modifier = Modifier.height(17.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(17.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                "기기 정보",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                "제조사 | ${lightDevice?.manufacturerCode}",
                fontSize = 12.sp
            )
            Text(
                "연결방식 | Wi-Fi",
                fontSize = 12.sp
            )
            Text(
                "기기 타입 | ${lightDevice?.deviceType}",
                fontSize = 12.sp
            )

        }

        Spacer(modifier = Modifier.height(100.dp))
    }

    when (powerState) {
        is ControlState.Error -> {}
        ControlState.Idle -> {

        }

        is ControlState.Loaded -> {
            checked = (powerState as ControlState.Loaded).data.activated
        }

        ControlState.Loading -> {
            LoadingComponent()
        }
    }

    when (brightnessState) {
        is LightBrightState.Error -> {}
        LightBrightState.Idle -> {}
        is LightBrightState.Loaded -> {
            brightness = (brightnessState as LightBrightState.Loaded).data.brightness
        }

        LightBrightState.Loading -> LoadingComponent()
    }

    when (colorState) {
        is LightColorState.Error -> {}
        LightColorState.Idle -> {}
        is LightColorState.Loaded -> {
            LaunchedEffect(colorState) {
                isColor = true
                val data = (colorState as LightColorState.Loaded).data
                controller.selectByHsv(
                    (data.hue * 36 / 10).toFloat(),
                    data.saturation / 100,
                    1f,
                    1f,
                    false
                )

            }
        }

        LightColorState.Loading -> LoadingComponent()
    }
    when(temperatureState){
        is LightTemperatureState.Error -> {}
        LightTemperatureState.Idle -> {}
        is LightTemperatureState.Loaded ->{

        }
        LightTemperatureState.Loading -> {
            LoadingComponent()
        }
    }

}