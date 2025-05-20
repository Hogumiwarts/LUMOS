package com.hogumiwarts.lumos.ui.screens.auth.onboarding

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo


@Composable
fun PermissionsScreen(
    onPermissionsGranted: () -> Unit = {}
) {

    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // 현재 표시할 권한 화면 상태 (0: 위치, 1: UWB)
    val currentPermissionScreen = remember { mutableStateOf(0) }

    // 권한 요청 상태 관리
    val context = LocalContext.current
    val shouldRequestPermissions = remember { mutableStateOf(false) }

    // 위치 권한 요청 런처
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 위치 권한이 허용되면 다음 화면으로
            currentPermissionScreen.value = 1
        }
    }

    // UWB(필요한 경우) 및 기타 권한 요청 런처
    val otherPermissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // 사용자가 모든 권한 설정을 마치면 홈 화면으로 이동
        onPermissionsGranted()
    }

    // 권한 요청 로직
    LaunchedEffect(shouldRequestPermissions.value) {
        if (shouldRequestPermissions.value) {
            if (currentPermissionScreen.value == 0) {
                // 위치 권한 요청
                val hasLocationPermission = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasLocationPermission) {
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                } else {
                    // 이미 권한이 있으면 다음 화면으로
                    currentPermissionScreen.value = 1
                }
            } else {
                // UWB 및 기타 권한 요청
                val permissions = mutableListOf<String>()

                // UWB 권한 추가 (API 34 이상에서만 사용 가능)
                if (android.os.Build.VERSION.SDK_INT >= 34) {
                    permissions.add("android.permission.UWB_RANGING")
                }

                // 블루투스 권한 추가 (Android 12 이상)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    permissions.add(Manifest.permission.BLUETOOTH_SCAN)
                    permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
                } else {
                    permissions.add(Manifest.permission.BLUETOOTH)
                    permissions.add(Manifest.permission.BLUETOOTH_ADMIN)
                }

                if (permissions.isNotEmpty()) {
                    otherPermissionsLauncher.launch(permissions.toTypedArray())
                } else {
                    // 요청할 권한이 없으면 완료 처리
                    onPermissionsGranted()
                }
            }

            // 요청 상태 초기화
            shouldRequestPermissions.value = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Image(
            painter = painterResource(id = R.drawable.bg_onboardings),
            contentDescription = "권한 페이지 배경",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    top = 32.dp,
                    bottom = 32.dp + navBarHeight
                )
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "앱 서비스 접근 권한 안내",
                fontFamily = nanum_square_neo,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 32.dp, bottom = 32.dp)
            )

            if (currentPermissionScreen.value == 0) {
                PermissionContent(
                    iconResId = R.drawable.ic_permission_location,
                    title = "위치",
                    description = "기기 간 거리와 방향 파악을 통한 정확한 자동화 제공",
                    secondTitle = "공간 인식 및 근접 기기 연결",
                    secondDescription = "UWB를 통한 기기 자동 제어 지원",
                    secondIconResId = R.drawable.ic_permission_uwb
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                buttonText = "시작하기",
                onClick = {
                    onPermissionsGranted()
                },
                modifier = Modifier.fillMaxWidth()
            )

        }
    }
}


@Composable
fun LocationPermissionContent() {
    PermissionContent(
        iconResId = R.drawable.ic_permission_location,
        title = "위치",
        description = "기기 간 거리와 방향 파악을 통한 정확한 자동화 제공"
    )
}

@Composable
fun UwbPermissionContent() {
    PermissionContent(
        iconResId = R.drawable.ic_permission_uwb,
        title = "공간 인식 및 근접 기기 연결",  // title로 변경
        description = "UWB를 통한 기기 자동 제어 지원"  // description으로 변경
    )
}

@Composable
fun PermissionContent(
    iconResId: Int,
    title: String? = null,
    description: String? = null,
    secondTitle: String? = null,
    secondDescription: String? = null,
    secondIconResId: Int = R.drawable.ic_permission_uwb
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(0x33FFFFFF),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, Color(0xff99A2D6), shape = RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                "필수 권한",
                color = Color(0xff99A2D6),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 첫 번째 권한 정보
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    if (title != null) {
                        Text(
                            text = title,
                            fontFamily = nanum_square_neo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (description != null) {
                        Text(
                            text = description,
                            fontFamily = nanum_square_neo,
                            fontSize = 11.sp,
                            color = Color(0xff99A2D6).copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // 두 번째 권한 정보
            if (secondTitle != null && secondDescription != null) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_permission_uwb),
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = secondTitle,
                            fontFamily = nanum_square_neo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = secondDescription,
                            fontFamily = nanum_square_neo,
                            fontSize = 12.sp,
                            color = Color(0xff99A2D6).copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "필수 권한의 경우 허용하지 않으면 주요 기능 사용이\n불가능하여 서비스 이용이 제한됩니다.",
        fontFamily = nanum_square_neo,
        fontSize = 12.sp,
        color = Color.White.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        lineHeight = 18.sp
    )
}


@Preview(showBackground = true)
@Composable
fun pvv() {
    PermissionsScreen({})
}