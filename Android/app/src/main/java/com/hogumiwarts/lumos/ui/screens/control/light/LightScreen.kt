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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import com.google.gson.annotations.SerializedName
import com.hogumiwarts.domain.model.light.LightStatusData
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.LoadingComponent
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.control.components.GradientColorSlider
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.LightPreviewViewModel
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.PreviewLightScreenContent
import com.hogumiwarts.lumos.ui.viewmodel.LightViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
    @SerializedName("lightCode") val lightCode: String,
)

@Composable
fun LightScreen(
    selectedDevice: MyDevice,
    previewMode: Boolean = false,
    navController: NavController

) {
    if (previewMode) { // 루틴 생성에서 띄울 화면
        val viewModel: LightPreviewViewModel = hiltViewModel()
        PreviewLightScreenContent(viewModel, selectedDevice, navController)
    } else { // 직접 제어 화면
        val viewModel: LightViewModel = hiltViewModel()
        RealLightScreenContent(viewModel, selectedDevice.deviceId.toLong())
    }
}

