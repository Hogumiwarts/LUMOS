package com.hogumiwarts.lumos.ui.screens.control.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.LoadingComponent
import com.hogumiwarts.lumos.ui.screens.control.light.LightIntent
import com.hogumiwarts.lumos.ui.screens.control.light.LightTemperatureState
import com.hogumiwarts.lumos.ui.viewmodel.LightViewModel

@Composable
fun GradientColorSlider(modifier: Modifier = Modifier,viewModel: LightViewModel = hiltViewModel()) {
    // 시작 색상 (F99D9D) 및 끝 색상 (98A7F2) 정의
    val startColor = Color(0xFFF99D9D) // 시작 색상
    val endColor = Color(0xFF98A7F2)   // 끝 색상

    val temperature by viewModel.temperature.collectAsState()
    val temperatureState by viewModel.temperatureState.collectAsState()
    var brightness by remember { mutableIntStateOf(0) }
    when(temperatureState){

        is LightTemperatureState.Error -> {}
        LightTemperatureState.Idle -> {}
        is LightTemperatureState.Loaded ->{
            LaunchedEffect(temperatureState) {
                val t= (temperatureState as LightTemperatureState.Loaded).data.temperature
                brightness= ((t-2200)*100/(6500-2200))
            }

        }
        LightTemperatureState.Loading -> {

        }
    }

    // 슬라이더 값 상태

    LaunchedEffect(temperature) {
        brightness = ((temperature-2200)*100/(6500-2200))
    }

    // 현재 슬라이더 값에 따른 색상 계산
    val currentColor by remember(brightness) {
        derivedStateOf {
            interpolateColor(startColor, endColor, brightness / 100f)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 불 아이콘
        Image(
            painter = painterResource(id = R.drawable.ic_fire),
            contentDescription = "Warm light",
            modifier = Modifier.size(24.dp)
        )

        // 슬라이더
        Slider(
            value = brightness.toFloat(),
            modifier = modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            onValueChange = {
                brightness = it.toInt()

            },
            onValueChangeFinished = {
                viewModel.sendIntent(LightIntent.ChangeLightTemperature(9,brightness*(6500-2200)/100+2200.toInt()))
            },
            valueRange = 0f..100f,
            steps = 0,
            colors = SliderDefaults.colors(
                thumbColor = currentColor,
                activeTrackColor = currentColor,
                inactiveTrackColor = Color.LightGray
            ),
        )

        // 얼음 아이콘
        Image(
            painter = painterResource(id = R.drawable.ic_ice),
            contentDescription = "Cool light",
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * 두 색상 사이를 비율에 따라 보간하는 함수
 * @param start 시작 색상
 * @param end 끝 색상
 * @param fraction 보간 비율 (0f ~ 1f)
 * @return 보간된 색상
 */
fun interpolateColor(start: Color, end: Color, fraction: Float): Color {
    // 비율을 0f~1f 범위로 제한
    val clampedFraction = fraction.coerceIn(0f, 1f)

    // 각 색상 채널(R, G, B) 보간
    val r = lerp(start.red, end.red, clampedFraction)
    val g = lerp(start.green, end.green, clampedFraction)
    val b = lerp(start.blue, end.blue, clampedFraction)

    return Color(r, g, b)
}

/**
 * 두 값 사이를 선형 보간하는 함수
 */
fun lerp(start: Float, end: Float, fraction: Float): Float {
    return start + (end - start) * fraction
}