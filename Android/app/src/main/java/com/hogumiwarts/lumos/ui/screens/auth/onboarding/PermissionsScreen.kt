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

    val context = LocalContext.current
    val permissionsRequested = remember { mutableStateOf(false) }
    val allPermissionsGranted = remember { mutableStateOf(false) }

    // 디버깅용 상태 추가
    val debugRequestStatus = remember { mutableStateOf("초기화됨") }

    // 모든 권한을 한 번에 요청하는 런처
    val permissionsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResult ->
        // 결과 처리 (디버깅 정보 추가)
        val resultStr = permissionsResult.entries.joinToString { "${it.key}: ${it.value}" }
        debugRequestStatus.value = "권한 결과: $resultStr"

        allPermissionsGranted.value = permissionsResult.all { it.value }
        permissionsRequested.value = true
    }

    // 권한 체크 및 요청 함수
    fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // 위치 권한 체크 (COARSE도 추가)
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // COARSE 위치 권한 추가 (더 낮은 수준의 권한도 요청)
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        // UWB 권한 체크 (API 34 이상)
        if (android.os.Build.VERSION.SDK_INT >= 34) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    "android.permission.UWB_RANGING"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add("android.permission.UWB_RANGING")
            }
        }

        // 블루투스 권한 체크
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Android 12 이상
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        } else {
            // Android 12 미만
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH)
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_ADMIN
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }

        // 디버깅 정보 추가
        debugRequestStatus.value = "요청 권한: ${permissionsToRequest.joinToString()}"

        if (permissionsToRequest.isNotEmpty()) {
            // 필요한 권한 요청
            permissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // 이미 모든 권한이 허용된 경우
            allPermissionsGranted.value = true
            permissionsRequested.value = true
            debugRequestStatus.value = "이미 모든 권한 있음"
        }
    }

    // 화면 첫 로드 시 자동으로 권한 요청 시작
    LaunchedEffect(Unit) {
        checkAndRequestPermissions()
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

            // 모든 권한 정보를 한 화면에 표시
            PermissionContent(
                iconResId = R.drawable.ic_permission_location,
                title = "위치",
                description = "기기 간 거리와 방향 파악을 통한 정확한 자동화 제공",
                secondTitle = "공간 인식 및 근접 기기 연결",
                secondDescription = "UWB를 통한 기기 자동 제어 지원",
                secondIconResId = R.drawable.ic_permission_uwb,
                thirdtitle = "블루투스",
                thirdDescription = "기기 연결 및 데이터 교환을 위한 필수 권한"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 디버깅용 텍스트 추가 (개발 중에만 사용)
//            Text(
//                text = debugRequestStatus.value,
//                fontFamily = nanum_square_neo,
//                fontSize = 11.sp,
//                color = Color.White,
//                textAlign = TextAlign.Center,
//                modifier = Modifier.padding(vertical = 8.dp)
//            )

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                buttonText = if (!allPermissionsGranted.value) "권한 허용하기" else "시작하기",
                onClick = {
                    if (!allPermissionsGranted.value) {
                        // 권한이 아직 허용되지 않았다면 권한 요청 시작/재시도
                        checkAndRequestPermissions()
                    } else {
                        // 모든 권한이 허용되었으면, 다음 화면으로
                        onPermissionsGranted()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PermissionContent(
    iconResId: Int,
    title: String? = null,
    description: String? = null,
    secondTitle: String? = null,
    secondDescription: String? = null,
    secondIconResId: Int = R.drawable.ic_permission_uwb,
    thirdtitle: String? = null,
    thirdDescription: String? = null
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

            if (thirdtitle != null && thirdDescription != null) {
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
                            text = thirdtitle,
                            fontFamily = nanum_square_neo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = thirdDescription,
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