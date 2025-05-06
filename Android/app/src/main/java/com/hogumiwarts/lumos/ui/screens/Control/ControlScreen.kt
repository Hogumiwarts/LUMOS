package com.hogumiwarts.lumos.ui.screens.Control

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.uwb.UwbAddress
import androidx.navigation.NavController
import com.hogumiwarts.lumos.ui.Common.CommonTopBar
import com.hogumiwarts.lumos.utils.uwb.UwbRangingManager
import com.hogumiwarts.lumos.utils.uwb.BleScanner
import com.hogumiwarts.lumos.utils.uwb.GattConnector
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ControlScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // 필요한 상태들
    var isScanning by remember { mutableStateOf(false) }
    var discovered by remember { mutableStateOf<List<BluetoothDevice>>(emptyList()) }
    var connected by remember { mutableStateOf<List<String>>(emptyList()) }
    var isRanging by remember { mutableStateOf(false) }
    var nearestTag by remember { mutableStateOf<TagInfo?>(null) }

    // 각종 매니저들 생성
    val uwbRangingManager = remember { UwbRangingManager(context) }
    val bleScanner = remember { BleScanner(context) }
    val gattConnector = remember { GattConnector(context, uwbRangingManager) }

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            Toast.makeText(context, "필요한 모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            // 스캔 시작
            startScanning(context, bleScanner, isScanning) { scanning, devices ->
                isScanning = scanning
                discovered = devices
            }
        } else {
            Toast.makeText(context, "앱 기능을 사용하려면 모든 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    // BLE 스캔 결과 수집
    LaunchedEffect(bleScanner) {
        bleScanner.scanResults.collect { device ->
            if (!discovered.contains(device)) {
                discovered = discovered + device
            }
        }
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                barTitle = "SmartTag2 제어",
                onBackClick = {
                    navController.popBackStack()
                },
                isAddBtnVisible = false,
                onAddClick = {})
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // UWB 지원 여부 표시
                val uwbSupported = uwbRangingManager.isUwbSupported()
                Text(
                    text = "UWB 지원: ${if (uwbSupported) "지원함" else "지원하지 않음"}",
                    fontSize = 16.sp,
                    color = if (uwbSupported) Color.Green else Color.Red,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 스캔 버튼
                Button(
                    onClick = {
                        if (isScanning) {
                            bleScanner.stopScan()
                            isScanning = false
                        } else {
                            checkAndRequestPermissions(context, permissionLauncher, bleScanner) { scanning, devices ->
                                isScanning = scanning
                                discovered = devices
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isScanning) Color.Red else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = if (isScanning) "스캔 중지" else "SmartTag2 스캔")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 레인징 버튼 (방향 찾기로 변경)
                Button(
                    onClick = {
                        if (isRanging) {
                            uwbRangingManager.stopRanging()
                            isRanging = false
                            nearestTag = null
                        } else if (connected.isNotEmpty()) {
                            coroutineScope.launch {
                                uwbRangingManager.startRanging(
                                    onDistanceUpdate = { address, distance, azimuth ->
                                        // 방위각이 작은 기기를 가장 가까운 태그로 판단
                                        if (abs(azimuth) < 10.0f) {
                                            nearestTag = TagInfo(address, distance, azimuth)

                                            // 거리가 가까운 경우에만 토스트 메시지 표시
                                            if (distance < 3.0f) {
                                                Toast.makeText(
                                                    context,
                                                    "방향의 태그 - 거리: ${String.format("%.2f", distance)}m",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    onError = { message ->
                                        Toast.makeText(context, "UWB 오류: $message", Toast.LENGTH_LONG).show()
                                        isRanging = false
                                    }
                                )
                                isRanging = true
                            }
                        } else {
                            Toast.makeText(context, "먼저 SmartTag2 기기를 연결해야 합니다.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = connected.isNotEmpty() || isRanging,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRanging) Color.Red else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(text = if (isRanging) "방향 탐색 중지" else "방향으로 태그 찾기")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 방향 태그 정보 표시
                if (isRanging && nearestTag != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "방향에서 찾은 태그",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("주소: ${nearestTag?.address}")
                            Text("거리: ${String.format("%.2f", nearestTag?.distance)}m")
                            Text("방위각: ${String.format("%.1f", nearestTag?.azimuth)}°")

                            // 방향 표시기
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val center = Offset(size.width / 2, size.height / 2)
                                    val radius = minOf(size.width, size.height) / 2 - 16.dp.toPx()

                                    // 외부 원 그리기
                                    drawCircle(
                                        color = Color.LightGray.copy(alpha = 0.3f),
                                        radius = radius,
                                        center = center
                                    )

                                    // 내부 원 그리기
                                    drawCircle(
                                        color = Color.LightGray.copy(alpha = 0.5f),
                                        radius = radius * 0.5f,
                                        center = center
                                    )

                                    // 방향 화살표 그리기
                                    val angle = (nearestTag?.azimuth ?: 0f) * Math.PI.toFloat() / 180f
                                    val arrowEnd = Offset(
                                        x = center.x + radius * sin(angle),
                                        y = center.y - radius * cos(angle)
                                    )

                                    // 화살표 선 그리기
                                    drawLine(
                                        color = Color.Red,
                                        start = center,
                                        end = arrowEnd,
                                        strokeWidth = 4.dp.toPx()
                                    )

                                    // 화살표 끝부분 그리기
                                    drawCircle(
                                        color = Color.Red,
                                        radius = 6.dp.toPx(),
                                        center = arrowEnd
                                    )
                                }
                            }
                        }
                    }
                } else if (isRanging) {
                    // 레인징 중이지만 아직 방향의 태그를 찾지 못한 경우
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("방향의 태그를 찾는 중...", fontSize = 14.sp)
                    }
                }

                // 스캔 중 표시
                if (isScanning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            strokeCap = StrokeCap.Round
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("스캔 중...", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 발견된 기기 목록 (레인징 중이 아닐 때만 표시)
                if (discovered.isNotEmpty() && !isRanging) {
                    Text(
                        text = "발견된 SmartTag2 기기 (${discovered.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn {
                        items(discovered) { device ->
                            val deviceName = getDeviceName(context, device)
                            val deviceAddress = device.address
                            val isConnected = connected.contains(deviceAddress)

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                border = BorderStroke(
                                    width = if (isConnected) 2.dp else 0.dp,
                                    color = if (isConnected) Color.Green else Color.Transparent
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = deviceName,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = deviceAddress,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            if (!isConnected) {
                                                coroutineScope.launch {
                                                    val success = connectToTag(device, gattConnector)
                                                    if (success) {
                                                        connected = connected + deviceAddress
                                                        Toast.makeText(
                                                            context,
                                                            "태그 연결 성공: $deviceName",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "태그 연결 실패: $deviceName",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            }
                                        },
                                        enabled = !isConnected && !isRanging,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isConnected) Color.Green else MaterialTheme.colorScheme.primary
                                        )
                                    ) {
                                        Text(text = if (isConnected) "연결됨" else "연결")
                                    }
                                }
                            }
                        }
                    }
                } else if (!isScanning && !isRanging) {
                    Text(
                        text = "스캔 버튼을 눌러 SmartTag2 기기를 찾으세요.",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(32.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// 권한 확인 및 요청
private fun checkAndRequestPermissions(
    context: Context,
    permissionLauncher: androidx.activity.result.ActivityResultLauncher<Array<String>>,
    bleScanner: BleScanner,
    onResult: (isScanning: Boolean, devices: List<BluetoothDevice>) -> Unit
) {
    val permissionsToRequest = mutableListOf<String>()

    // BLE 권한 확인
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        // Android 12 이상에서는 BLUETOOTH_SCAN, BLUETOOTH_CONNECT 권한 필요
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
    } else {
        // 이전 버전에서는 BLUETOOTH, BLUETOOTH_ADMIN 권한 필요
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH)
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
        }
    }

    // 위치 권한 확인 (BLE 스캔에 필요)
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    // UWB 권한 확인
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.UWB_RANGING) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.UWB_RANGING)
    }

    if (permissionsToRequest.isNotEmpty()) {
        permissionLauncher.launch(permissionsToRequest.toTypedArray())
    } else {
        // 이미 모든 권한이 허용됨
        startScanning(context, bleScanner, false, onResult)
    }
}

// BLE 스캔 시작
private fun startScanning(
    context: Context,
    bleScanner: BleScanner,
    isCurrentlyScanning: Boolean,
    onResult: (isScanning: Boolean, devices: List<BluetoothDevice>) -> Unit
) {
    if (!bleScanner.isBleSupported()) {
        Toast.makeText(context, "이 기기는 BLE를 지원하지 않습니다.", Toast.LENGTH_LONG).show()
        return
    }

    if (!bleScanner.isBleEnabled()) {
        Toast.makeText(context, "블루투스를 활성화해주세요.", Toast.LENGTH_LONG).show()
        return
    }

    if (!bleScanner.hasRequiredPermissions()) {
        Toast.makeText(context, "필요한 권한이 없습니다.", Toast.LENGTH_LONG).show()
        return
    }

    bleScanner.startScan()
    onResult(true, emptyList())
    Toast.makeText(context, "SmartTag2 스캔 시작...", Toast.LENGTH_SHORT).show()
}

// SmartTag2에 연결
private suspend fun connectToTag(device: BluetoothDevice, gattConnector: GattConnector): Boolean {
    return gattConnector.connectAndRetrieveOobParameters(device)
}

// 기기 이름 가져오기 (권한 확인 포함)
private fun getDeviceName(context: Context, device: BluetoothDevice): String {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
        ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        "Unknown (권한 부족)"
    } else {
        try {
            device.name ?: "Unknown"
        } catch (e: SecurityException) {
            "Unknown (권한 오류)"
        }
    }
}

// 태그 정보 데이터 클래스
data class TagInfo(
    val address: UwbAddress,
    val distance: Float,
    val azimuth: Float
)