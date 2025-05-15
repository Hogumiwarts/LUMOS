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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.gson.annotations.SerializedName
import com.hogumiwarts.domain.model.light.LightStatusData
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.control.components.GradientColorSlider
import com.hogumiwarts.lumos.ui.viewmodel.LightViewModel

data class LightDevice(
    @SerializedName("tagNumber") val tagNumber: Int,
    @SerializedName("deviceId") val deviceId: Int,
    @SerializedName("deviceImg") val deviceImg: String,
    @SerializedName("deviceName") val deviceName: String,
    @SerializedName("manufacturerCode") val manufacturerCode: String,
    @SerializedName("deviceModel") val deviceModel: String,
    @SerializedName("deviceType") val deviceType: String,
    @SerializedName("activated") val activated: Boolean,
    @SerializedName("brightness") val brightness: Int,
    @SerializedName("lightTemperature") val lightTemperature: String,
    @SerializedName("lightCode") val lightCode: String
)

@Composable
fun LightScreen(viewModel: LightViewModel = hiltViewModel()) {

    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.saveJwt("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MzE2MzA4LCJleHAiOjE3NDc0MDI3MDh9.EV6G_bku_EsHvXGIViGVrqH_kdWw9Tvj6g9VE1k_kgw","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MzE2MzA4LCJleHAiOjE3NDc5MjExMDh9.ECpyX4l16VV5jnd0bPtR31yKQmV8CpALN1uJifMvdDc")
        viewModel.sendIntent(LightIntent.LoadLightStatus(4))
    }

    val state by viewModel.state.collectAsState()

    var checked by remember { mutableStateOf(false) }
    var lightDevice by remember { mutableStateOf<LightStatusData?>(null) }
    var brightness by remember { mutableIntStateOf(0) }
    var selectedColorCode by remember { mutableStateOf("#FFFFFF") }
    var selectedColor by remember { mutableStateOf(Color.White) }

    val controller = rememberColorPickerController()

    when(state){
        is LightStatusState.Error -> {}
        LightStatusState.Idle -> {}
        is LightStatusState.Loaded -> {
            LaunchedEffect(state) {
                val data = (state as LightStatusState.Loaded).data
                checked = data.activated
                brightness= data.brightness
                controller.selectByHsv((data.hue*36/10).toFloat(), data.saturation/100, 1f, 1f, false)
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
            text = lightDevice?.deviceName?:"",
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
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = "색상 설정",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(10.dp),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    selectedColorCode = "#" + colorEnvelope.hexCode.substring(2)
                    selectedColor = colorEnvelope.color
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

}

@Composable
@Preview(showBackground = true)
fun LightScreenPreview() {
    LightScreen()
}