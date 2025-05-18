package com.hogumiwarts.lumos.ui.screens.control.light

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.annotations.SerializedName
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.LightPreviewViewModel
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.devicecontrolscreen.PreviewLightScreenContent
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
    @SerializedName("lightCode") val lightCode: String,
)

@Composable
fun LightScreen(
    selectedDevice: MyDevice,
    previewMode: Boolean = false,
    navController: NavController

) {
    if (previewMode) { // 루틴 생성에서 띄울 화면
        PreviewLightScreenContent(selectedDevice, navController)
    } else { // 직접 제어 화면
        val viewModel: LightViewModel = hiltViewModel()
        RealLightScreenContent(viewModel, selectedDevice.deviceId.toLong())
    }
}

