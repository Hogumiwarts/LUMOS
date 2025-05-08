package com.hogumiwarts.lumos.ui.screens.control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hogumiwarts.lumos.ui.common.CommonTopBar
import com.hogumiwarts.lumos.utils.uwb.BleDevice


@Composable
fun ControlScreen(
    navController: NavController,
    bleViewModel: BleScannerViewModel = hiltViewModel()
) {

    val devices by bleViewModel.devices.collectAsState()

    Scaffold(
        topBar = {
            CommonTopBar(
                barTitle = "SmartTag2 제어",
                onBackClick = {
                    navController.popBackStack()
                },
                isAddBtnVisible = false,
                onAddClick = {}
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.Gray)
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

            // 탐지된 기기 리스트
            if (devices.isEmpty()) {
                Text(
                    "주변 BLE 기기를 찾는 중입니다…",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(devices) { d: BleDevice ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(
                                    text = d.name ?: "이름 없음",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    text = d.address,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "RSSI: ${d.rssi} dBm",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}