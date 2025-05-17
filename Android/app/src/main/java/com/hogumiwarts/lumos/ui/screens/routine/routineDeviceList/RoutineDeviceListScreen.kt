package com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.ui.common.CommonDialog
import com.hogumiwarts.lumos.ui.common.DeviceGridSection
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.control.light.LightIntent
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineDeviceListScreen(
    viewModel: RoutineDeviceListViewModel = hiltViewModel(),
    devices: List<MyDevice>,
    onSelectComplete: (MyDevice) -> Unit,
    showDuplicateDialog: Boolean,
    onDismissDuplicateDialog: () -> Unit,
    navController: NavController
) {
    // 선택 기기 상태
    val selectedDeviceId by viewModel.selectedDeviceId
    val showDialog by viewModel.showDialog
    val deviceList by viewModel.devices
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("commandDeviceJson")
            ?.observe(lifecycleOwner) { json ->
                val device = Gson().fromJson(json, CommandDevice::class.java)
                val myDevice = MyDevice(
                    deviceId = device.deviceId,
                    deviceName = device.deviceName,
                    isOn = device.commands.find { it.capability == "switch" }?.command == "on",
                    isActive = true,
                    deviceType = DeviceListType.valueOf(device.deviceType),
                    commands = device.commands
                )
                onSelectComplete(myDevice)
            }
    }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .statusBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // topBar
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "나의 기기 목록",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = nanum_square_neo
            )

        }


        // 기기 목록
        DeviceGridSection(
            devices = deviceList,
            selectedDeviceId = selectedDeviceId,
            onDeviceClick = { viewModel.onDeviceClicked(it) }
        )

        Spacer(modifier = Modifier.height(125.dp))

        // 선택 버튼
        PrimaryButton(
            buttonText = "선택하기",
            onClick = {
                viewModel.getSelectedDevice()?.let { selected ->
                    navController.currentBackStackEntry?.savedStateHandle?.set(
                        "selectedDevice",
                        selected
                    )

                    when (selected.deviceType) {
                        DeviceListType.LIGHT -> {
                            navController.navigate("light_control?preview=true")
                        }

                        DeviceListType.AIRPURIFIER -> {
                            navController.navigate("airpurifier_control?preview=true")
                        }

                        else -> {
                            // TODO: 지원하지 않는 기기일 경우 처리 (예: 다이얼로그, 토스트 등)
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(28.dp))

        // 다이얼로그 설정
        CommonDialog(
            showDialog = showDialog,
            onDismiss = { viewModel.dismissDialog() },
            titleText = "선택할 수 없는 기기예요!",
            bodyText = "기기 상태가 비활성화로 감지되어 제어할 수 없습니다. 거리가 멀어지면 비활성화로 전환될 수 있어요."
        )

        // 중복 기기용 다이얼로그
        CommonDialog(
            showDialog = showDuplicateDialog,
            onDismiss = onDismissDuplicateDialog,
            titleText = "이미 선택한 기기예요!",
            bodyText = "같은 기기 + 같은 상태 조합은 한 번만 사용할 수 있어요. 새로운 조합으로 시도해볼까요? ✨"
        )
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun RoutineDeviceListScreenPreview() {
//    RoutineDeviceListScreen(
//        devices = MyDevice.sample,
//        onSelectComplete = {},
//    )
//}