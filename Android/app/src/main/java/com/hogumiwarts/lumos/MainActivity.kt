package com.hogumiwarts.lumos

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.uwb.UwbManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.common.SecondaryButton
import com.hogumiwarts.lumos.ui.navigation.NavGraph
import com.hogumiwarts.lumos.ui.screens.devices.DeviceListViewModel
import com.hogumiwarts.lumos.ui.screens.control.UwbRanging
import com.hogumiwarts.lumos.ui.screens.control.light.RealLightScreenContent
import com.hogumiwarts.lumos.ui.screens.gesture.GestureScreen
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var tokenDataStore: TokenDataStore
    private val deviceListViewModel: DeviceListViewModel by viewModels()

    // 네트워크
    private val _isNetworkAvailable = mutableStateOf(false)
    private val connectivityManager by lazy {
        getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _isNetworkAvailable.value = true
        }

        override fun onLost(network: Network) {
            _isNetworkAvailable.value = false
        }
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)

        // 현재 네트워크 상태 설정
        _isNetworkAvailable.value = isNetworkAvailable()
    }

    private fun unregisterNetworkCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Timber.e(e, "네트워크 콜백 해제 오류")
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

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
                    Timber.tag("smartthings")
                        .d("🪄 연동 완료!! :: installedAppId - $installedAppId, authToken - $authToken")
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
//            checkAndRequestPermissions()
//            Toast.makeText(this, "앱 기능을 사용하려면 모든 권한이 필요합니다.", Toast.LENGTH_LONG).show()
        }
    }

    lateinit var uwbManager: UwbManager // UWB 관리자 객체

    @Inject
    lateinit var uwbRanging: UwbRanging // UWB 레인징 객체

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val deviceId = intent.getLongExtra("deviceId", -1L) // 기본값 -1
        val deviceType = intent.getStringExtra("deviceType") ?: ""


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
//        checkAndRequestPermissions()

        // 네트워크 콜백 등록
        registerNetworkCallback()

        setContent {
            LUMOSTheme {

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color.Transparent
                ) {

                    NetworkConnectivityWrapper {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // 네비게이션 바 높이를 받아와서 그만큼 하단 여백 추가
                            val navBarHeight = WindowInsets.navigationBars.asPaddingValues()
                                .calculateBottomPadding()

                            MainScreen(deviceId, deviceType)

                            Spacer(modifier = Modifier.height(navBarHeight))
                        }
                    }
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
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH)
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }

        // 위치 권한 확인 (BLE 스캔에 필요)
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // UWB 권한 확인
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.UWB_RANGING
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
        unregisterNetworkCallback()
    }

    // 네트워크 연결 상태를 확인
    @Composable
    fun NetworkConnectivityWrapper(content: @Composable () -> Unit) {
        val isNetworkAvailable = remember { _isNetworkAvailable }
        var showNetworkDialog by remember { mutableStateOf(false) }

        // 네트워크 상태 변화 감지
        DisposableEffect(isNetworkAvailable.value) {
            showNetworkDialog = !isNetworkAvailable.value
            onDispose { }
        }

        // 메인 콘텐츠
        content()

        // 네트워크 연결 실패 시 팝업 표시
        if (showNetworkDialog) {
            AlertDialog(
                onDismissRequest = { /* 사용자가 백 버튼이나 바깥을 탭했을 때 - 팝업 유지 */  },
                title = {
                    Text(
                        text = "네트워크 연결 오류",
                        fontSize = 18.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "인터넷 연결이 끊어졌습니다. 와이파이나 모바일 데이터 연결을 확인해주세요.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(400),
                        color = Color(0x80151920),
                        textAlign = TextAlign.Center
                    )
                },
                confirmButton = {
                    PrimaryButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        buttonText = "다시 시도",
                        onClick = {
                            // 네트워크 다시 확인
                            _isNetworkAvailable.value = isNetworkAvailable()
                            if (_isNetworkAvailable.value) {
                                showNetworkDialog = false
                            }
                        }
                    )
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp),
            )
        }
    }


}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {
        val navController = rememberNavController()
        NavGraph(1, "", navController = navController)
    }
}