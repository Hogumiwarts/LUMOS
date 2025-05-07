package com.hogumiwarts.lumos

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.hogumiwarts.lumos.utils.uwb.UwbRangingManager
import com.hogumiwarts.lumos.utils.uwb.GattConnector
import com.hogumiwarts.lumos.utils.uwb.BleScanner
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var uwbRangingManager: UwbRangingManager
    private lateinit var bleScanner: BleScanner

    // 여러 권한을 한 번에 요청하는 런처
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "필요한 모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            checkSupport()
        } else {
            Toast.makeText(this, "앱 기능을 사용하려면 모든 권한이 필요합니다.", Toast.LENGTH_LONG).show()
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
            checkSupport()
        }
    }


    private fun checkSupport() {
        if (uwbRangingManager.isUwbSupported()) {
            Toast.makeText(this, "UWB를 지원합니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "UWB를 지원하지 않습니다.", Toast.LENGTH_LONG).show()
        }

        if (bleScanner.isBleSupported()) {
            if (bleScanner.isBleEnabled()) {
                Toast.makeText(this, "BLE가 활성화되어 있습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "BLE가 비활성화되어 있습니다.", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "BLE를 지원하지 않습니다.", Toast.LENGTH_LONG).show()
        }
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {

    }
}