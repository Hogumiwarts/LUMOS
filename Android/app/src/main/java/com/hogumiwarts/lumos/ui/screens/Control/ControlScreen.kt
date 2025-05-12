package com.hogumiwarts.lumos.ui.screens.control

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hogumiwarts.lumos.ui.common.CommonTopBar
import com.hogumiwarts.lumos.utils.uwb.BleDevice
import com.hogumiwarts.lumos.utils.uwb.GattConnector
import com.hogumiwarts.lumos.utils.uwb.SavedUwbDevice
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun ControlScreen(
    navController: NavController,
    bleViewModel: BleScannerViewModel = hiltViewModel()
) {

    val devices by bleViewModel.devices.collectAsState()
    val savedDevices by bleViewModel.savedDevices.collectAsState()
    val connectionState by bleViewModel.connectionState.collectAsState()
    val selectedDevice by bleViewModel.selectedDevice.collectAsState()

    // 컴포넌트가 처음 표시될 때 저장된 기기 로드
    LaunchedEffect(Unit) {
        bleViewModel.loadSavedDevices()
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                barTitle = "UWB 제어",
                onBackClick = {
                    navController.popBackStack()
                },
                isRightBtnVisible = false,
                onRightBtnClick = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(16.dp)
        ) {
            // 스캔 제어 버튼
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 전체 스캔
                Button(onClick = { bleViewModel.startScan() }) {
                    Text("스캔 시작")
                }
                // DWM3001-CDK 필터 스캔
                OutlinedButton(onClick = { bleViewModel.startScan(onlyDwm = true) }) {
                    Text("DWM 전용")
                }
                // 스캔 중지
                Button(onClick = { bleViewModel.stopScan() }) {
                    Text("스캔 중지")
                }
            }

            Spacer(Modifier.height(16.dp))

            // 연결 상태 표시
            when (connectionState) {
                GattConnector.ConnectionState.CONNECTING ->
                    Text("연결 중...", color = Color.Yellow)
                GattConnector.ConnectionState.CONNECTED ->
                    Text("연결됨", color = Color.Green)
                GattConnector.ConnectionState.SERVICES_DISCOVERED ->
                    Text("서비스 탐색 완료", color = Color.Green)
                GattConnector.ConnectionState.READY ->
                    Text("UWB 준비 완료", color = Color.Green)
                GattConnector.ConnectionState.FAILED ->
                    Text("연결 실패", color = Color.Red)
                else -> {}
            }

            if (selectedDevice != null) {
                Text("선택된 기기: ${selectedDevice?.name ?: "이름 없음"} (${selectedDevice?.address})")
            }

            Spacer(Modifier.height(16.dp))

            // 저장된 UWB 기기 목록 섹션
            Text(
                "저장된 UWB 기기",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))

            if (savedDevices.isEmpty()) {
                Text(
                    "저장된 UWB 기기가 없습니다.",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth()
                ) {

                    items(savedDevices) { device ->
                        SavedDeviceCard(
                            device = device,
                            onConnect = { bleViewModel.connectToSavedDevice(device) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 탐지된 기기 리스트 (기존 코드 수정)
            Text(
                "BLE 스캔 결과",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black
            )

            Spacer(Modifier.height(8.dp))

            // 탐지된 기기 리스트
            if (devices.isEmpty()) {
                Text(
                    "주변 BLE 기기를 찾는 중입니다…",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxWidth()
                ) {
                    items(devices) { device ->
                        ScannedDeviceCard(
                            device = device,
                            isSelected = selectedDevice?.address == device.address,
                            onSelect = { bleViewModel.connectToDevice(device) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun SavedDeviceCard(
    device: SavedUwbDevice,
    onConnect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onConnect)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "저장된 UWB 기기",
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "등록됨",
                    tint = Color.Green
                )
            }

            Text(
                text = device.address,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "채널: ${device.uwbParams.channel}",
                style = MaterialTheme.typography.bodySmall
            )

            // 마지막 연결 시간 표시
            if (device.lastConnected > 0) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val lastConnectedText = dateFormat.format(Date(device.lastConnected))
                Text(
                    text = "마지막 연결: $lastConnectedText",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun ScannedDeviceCard(
    device: BleDevice,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(
                text = device.name ?: "이름 없음",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = device.address,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "RSSI: ${device.rssi} dBm",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}