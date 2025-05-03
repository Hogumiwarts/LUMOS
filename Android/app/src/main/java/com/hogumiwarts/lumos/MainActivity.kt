package com.hogumiwarts.lumos

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme
import dagger.hilt.android.AndroidEntryPoint
import com.hogumiwarts.lumos.utils.UwbRangingManager
import com.hogumiwarts.lumos.utils.uwb.BleScanner
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var uwbRangingManager: UwbRangingManager
    private lateinit var bleScanner: BleScanner

    // 발견된 SmartTag2 기기 목록
    private val discoveredTags = mutableListOf<BluetoothDevice>()

    // 여러 권한을 한 번에 요청하는 런처
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "필요한 모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            initializeUwb()
        } else {
            Toast.makeText(this, "앱 기능을 사용하려면 모든 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    // 블루투스 활성화 요청 런처
    private val requestBluetoothEnableLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // 블루투스가 활성화됨
            startBleScan()
        } else {
            // 블루투스 활성화가 거부됨
            Toast.makeText(this, "블루투스 기능을 사용하려면 블루투스를 활성화해야 합니다.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //enableEdgeToEdge()

        // 시스템 바 영역까지 앱이 확장되도록
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 상태바, 네비게이션 바 배경을 투명하게 설정
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // 상태바, 네비게이션 바 아이콘 색상 설정 (false: 아이콘 흰색)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false

        // UWB, BLE 매니저 초기화
        uwbRangingManager = UwbRangingManager(this)
        bleScanner = BleScanner(this)

        // 권한 확인 및 요청
        checkAndRequestPermissions()


        setContent {
            LUMOSTheme {

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color.Transparent
                ) {
                    MainScreen()
                }
            }
        }

// BLE 스캔 결과 수집
        lifecycleScope.launch {
            bleScanner.scanResults.collect { device ->
                if (!discoveredTags.contains(device)) {
                    discoveredTags.add(device)

                    // 권한 확인 후 디바이스 이름 가져오기
                    val deviceName = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                        ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        "Unknown (권한 부족)"
                    } else {
                        try {
                            device.name ?: "Unknown"
                        } catch (e: SecurityException) {
                            "Unknown (권한 오류)"
                        }
                    }

                    Toast.makeText(this@MainActivity, "태그 발견: $deviceName", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // BLE 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 이상에서는 BLUETOOTH_SCAN, BLUETOOTH_CONNECT 권한 필요
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        } else {
            // 이전 버전에서는 BLUETOOTH, BLUETOOTH_ADMIN 권한 필요
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }

        // 위치 권한 확인 (BLE 스캔에 필요)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // UWB 권한 확인
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.UWB_RANGING) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.UWB_RANGING)
        }

        // 필요한 권한이 있으면 요청
        if (permissionsToRequest.isNotEmpty()) {
            requestMultiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // 이미 모든 권한이 허용됨
            initializeUwb()
        }
    }

    private fun initializeUwb() {
        // UWB 지원 여부 확인
        if (uwbRangingManager.isUwbSupported()) {
            uwbRangingManager.initialize()
            Toast.makeText(this, "UWB가 지원되며 초기화되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "이 기기는 UWB를 지원하지 않습니다.", Toast.LENGTH_LONG).show()
        }

        // BLE 초기화 및 스캔 시작
        if (bleScanner.isBleSupported()) {
            // BLE가 활성화되어 있는지 확인
            if (bleScanner.isBleEnabled()) {
                startBleScan()
            } else {
                // 블루투스 활성화 요청
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                requestBluetoothEnableLauncher.launch(enableBtIntent)
            }
        } else {
            Toast.makeText(this, "이 기기는 BLE를 지원하지 않습니다.", Toast.LENGTH_LONG).show()
        }
    }

    // BLE 스캔 시작
    private fun startBleScan() {
        bleScanner.startScan()
        Toast.makeText(this, "SmartTag2 스캔 시작...", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // 레인징 중지 및 리소스 정리
        if (::uwbRangingManager.isInitialized && uwbRangingManager.isRanging) {
            uwbRangingManager.stopRanging()
        }

        if (::bleScanner.isInitialized) {
            bleScanner.stopScan()
        }
    }



}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {

    }
}