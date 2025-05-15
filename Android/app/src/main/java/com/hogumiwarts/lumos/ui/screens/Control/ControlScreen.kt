package com.hogumiwarts.lumos.ui.screens.control

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
    bleViewModel: BleScannerViewModel = hiltViewModel(),
    controlViewModel: ControlViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val devices by bleViewModel.devices.collectAsState()
    val savedDevices by bleViewModel.savedDevices.collectAsState()
    val connectionState by bleViewModel.connectionState.collectAsState()
    val selectedDevice by bleViewModel.selectedDevice.collectAsState()


    val sessionReady = controlViewModel.sessionReady

    val scrollState = rememberScrollState()

    // UWB 기기 주소값
    var destinationAddress by remember { mutableStateOf("00:00") }


    // 컴포넌트가 처음 표시될 때 저장된 기기 로드
    LaunchedEffect(Unit) {
        bleViewModel.loadSavedDevices()
        if (controlViewModel.rangingActive) {
            Toast.makeText(
                context,
                "Ranging session active!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            controlViewModel.prepareSession()
        }
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
                .verticalScroll(scrollState)
        ) {
            // 상단 카드: UWB 상태 정보
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "UWB 장치 연결",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 로컬 주소 표시
                    Text("Local address: ${controlViewModel.localAddress}")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            if (controlViewModel.rangingActive) {
                                Toast.makeText(
                                    context,
                                    "Ranging session active!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                controlViewModel.prepareSession()
                            }
                        }) {
                            Text("세션 준비")
                        }
                        Button(onClick = {
                            controlViewModel.resetSession()
                        }) {
                            Text("세션 초기화")
                        }

                    }

                }
            }

            // 레인징 제어
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("설정된 컨트롤리 주소: ${controlViewModel.controleeAddresses.joinToString(", ")}")
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                if (
                                    !controlViewModel.startSingleRanging()
//                                    !controlViewModel.startRanging()
                                ) {
                                    Toast.makeText(
                                        context,
                                        "세션이 초기화되지 않았습니다!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            enabled = sessionReady && !controlViewModel.rangingActive
                        ) {
                            Text("멀티 레인징 시작")
                        }

                        Button(
                            onClick = { controlViewModel.stopRanging() },
                            enabled = controlViewModel.rangingActive
                        ) {
                            Text("레인징 중지")
                        }
                    }
                }
            }

            // 모든 장치의 레인징 결과
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "레인징 결과",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 연결된 장치 수 표시
                    Text(
                        text = if (controlViewModel.connectedDevices.isEmpty())
                            "연결된 장치가 없습니다"
                        else
                            "연결된 장치: ${controlViewModel.connectedDevices.size}개",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (controlViewModel.connectedDevices.isEmpty()) Color.Red else Color.Green
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 각 장치별 결과 표시 - 항상 모든 장치를 표시
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        controlViewModel.controleeAddresses.forEach { address ->
                            val position = controlViewModel.getDevicePosition(address)
                            val isConnected = position != null

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isConnected)
                                        Color(0xFFE3F2FD) // 연결됨 - 밝은 파란색
                                    else
                                        Color(0xFFEEEEEE) // 연결 안됨 - 회색
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "장치: $address",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text(
                                            text = if (isConnected) "연결됨" else "연결 안됨",
                                            color = if (isConnected) Color.Green else Color.Red
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    if (position != null) {
                                        // 거리 정보
                                        Row {
                                            Text(
                                                text = "거리:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.width(80.dp)
                                            )
                                            Text(
                                                text = "${position.distance?.value ?: "N/A"} m",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        // 방위각 정보
                                        Row {
                                            Text(
                                                text = "방위각:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.width(80.dp)
                                            )
                                            Text(
                                                text = "${position.azimuth?.value ?: "N/A"} °",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        // 고도 정보
                                        Row {
                                            Text(
                                                text = "고도:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.width(80.dp)
                                            )
                                            Text(
                                                text = "${position.elevation?.value ?: "N/A"} °",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }

                                        // 경과 시간 정보
                                        Row {
                                            Text(
                                                text = "경과 시간:",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.width(80.dp)
                                            )
                                            Text(
                                                text = "${position.elapsedRealtimeNanos} ns",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = "데이터 없음",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }


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
                            onConnect = {
                                bleViewModel.stopScan()
                                bleViewModel.connectToSavedDevice(device)
                            }
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