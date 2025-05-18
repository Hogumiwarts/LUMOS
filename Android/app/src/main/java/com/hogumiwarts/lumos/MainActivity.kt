package com.hogumiwarts.lumos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.content.ContextCompat
import androidx.core.uwb.UwbManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.ui.navigation.BottomNavigation
import com.hogumiwarts.lumos.ui.navigation.NavGraph
import com.hogumiwarts.lumos.ui.screens.auth.onboarding.WelcomeScreen
import com.hogumiwarts.lumos.ui.screens.control.AirpurifierScreen
import com.hogumiwarts.lumos.ui.screens.control.ControlScreen
import com.hogumiwarts.lumos.ui.screens.control.FindDeviceScreen
import com.hogumiwarts.lumos.ui.screens.control.SpeakerScreen
import com.hogumiwarts.lumos.ui.screens.devices.DeviceListViewModel
import com.hogumiwarts.lumos.ui.screens.control.UwbRanging
import com.hogumiwarts.lumos.ui.screens.control.light.RealLightScreenContent
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenDataStore: TokenDataStore
    private val deviceListViewModel: DeviceListViewModel by viewModels()


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleSmartThingsRedirect(intent)
    }

    override fun onStart() {
        super.onStart()

        // 앱 cold start 시도 대비
        handleSmartThingsRedirect(intent)
    }

    private fun handleSmartThingsRedirect(intent: Intent?) {
        val uri = intent?.data ?: return

        Timber.tag("smartthings").d("🧭 Redirect URI = $uri")

        if (uri.scheme == "smartthingslogin" && uri.host == "oauth-callback") {
            val installedAppId = uri.getQueryParameter("installedAppId")
            val name = uri.getQueryParameter("name")
            val authToken = uri.getQueryParameter("authToken")

            Timber.tag("smartthings").d("🔥 installedAppId in MainActivity = $installedAppId")
            Timber.tag("smartthings").d("🔥 name: $name, authToken: $authToken")

            if (!installedAppId.isNullOrEmpty() && !authToken.isNullOrEmpty()) {
                lifecycleScope.launch {
                    tokenDataStore.saveSmartThingsTokens(
                        installedAppId,
                        authToken,
                        name ?: "Unknown"
                    )

                    deviceListViewModel.checkAccountLinked()

                    Toast.makeText(
                        this@MainActivity,
                        "SmartThings 연동 완료!",
                        Toast.LENGTH_SHORT
                    ).show()
                    Timber.tag("smartthings").d("🪄 연동 완료!! :: installedAppId - $installedAppId, authToken - $authToken")
                }
            }
        }
    }


    // 여러 권한을 한 번에 요청하는 런처
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
//            Toast.makeText(this, "필요한 모든 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()

        } else {
            checkAndRequestPermissions()
//            Toast.makeText(this, "앱 기능을 사용하려면 모든 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    lateinit var uwbManager : UwbManager // UWB 관리자 객체
    @Inject lateinit var uwbRanging : UwbRanging // UWB 레인징 객체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId = intent.getLongExtra("deviceId", -1L) // 기본값 -1
        val deviceType = intent.getStringExtra("deviceType") ?: ""

        Log.d("MainActivity", "받은 deviceId: $deviceId")
        Log.d("MainActivity", "받은 deviceType: $deviceType")

        // 시스템 바 영역까지 앱이 확장되도록
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 상태바, 네비게이션 바 배경을 투명하게 설정
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // 상태바, 네비게이션 바 아이콘 색상 설정 (false: 아이콘 흰색)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = true
        controller.isAppearanceLightNavigationBars = true

        // 권한 확인 및 요청
        checkAndRequestPermissions()

        setContent {
            LUMOSTheme {

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color.Transparent
                ) {

                        MainScreen(deviceId, deviceType)

                }
            }
        }
    }


    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // BLE 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 이상에서는 BLUETOOTH_SCAN, BLUETOOTH_CONNECT 권한 필요
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
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

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        uwbRanging.cleanupAll()
    }


}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {
        val navController = rememberNavController()
        NavGraph(1,"",navController = navController)
    }
}