// 필요한 컴포즈 및 리소스 관련 import
package com.hogumiwarts.lumos.presentation.ui.screens.devices

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme

//

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.items
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.hogumiwarts.lumos.data.DevicesData


// 기기 정보를 담는 데이터 클래스 (기기명, 상태, 아이콘)
data class DeviceItem(val name: String, val status: Boolean, val icon: ImageVector)



// 메인 화면 컴포저블
@OptIn(ExperimentalHorologistApi::class)
@Composable
fun DevicesScreen(navController: NavHostController) {

    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.SingleButton,
            last = ItemType.Unspecified,
        ),
    )

    // 추후 ViewModel 또는 실제 데이터 연동으로 교체 예정
    val devices = listOf(
        DevicesData(
            tagNumber = 1001L,
            deviceId = 1L,
            installedAppId = "app.led.room",
            deviceImg = "icon_home", // or actual URL if using image loading
            deviceName = "게임방 무드등",
            activated = true
        ),
        DevicesData(
            tagNumber = 1002L,
            deviceId = 2L,
            installedAppId = "app.mood.player",
            deviceImg = "icon_home",
            deviceName = "무드 플레이어",
            activated = false
        ),
        DevicesData(
            tagNumber = 1003L,
            deviceId = 3L,
            installedAppId = "app.air.living",
            deviceImg = "icon_home",
            deviceName = "게임방 무드등",
            activated = true
        ),
        DevicesData(
            tagNumber = 1004L,
            deviceId = 4L,
            installedAppId = "app.home.camera",
            deviceImg = "icon_home",
            deviceName = "홈카메라",
            activated = true
        ),
        DevicesData(
            tagNumber = 1005L,
            deviceId = 5L,
            installedAppId = "app.air.living2",
            deviceImg = "icon_home",
            deviceName = "거실 공기청정기",
            activated = false
        )
    )


    // 활성화 여부에 따라 리스트 분리
    val (activatedDevices, deactivatedDevices) = devices.partition { it.activated }

    ScreenScaffold(
        scrollState = listState,
    ) {
// 배경 이미지와 전체 레이아웃 박스
        Box(modifier = Modifier.clip(CircleShape).fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.device_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )

            // 스크롤 가능한 리스트
            ScalingLazyColumn(
                columnState = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            ) {
                // 상단 타이틀 + 활성화 영역 구분선

                item {
                    Text(
                        text = "나의 기기 목록",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,

                    )
                }
                item {
                    DividerWithLabel("활성화")
                }

                // 활성화된 기기 리스트 렌더링
                items(activatedDevices) {
                    DeviceCard(it,navController)
                }

                // 비활성화 영역 구분선
                item {
                    DividerWithLabel("비활성화")
                }

                // 비활성화된 기기 리스트 렌더링
                items(deactivatedDevices) {
                    DeviceCard(it,navController)
                }

                // 마지막 여백
                item {

                    WearIconButton({})
                }
            }
        }
    }

}

// 외곽선 그라디언트 브러시 정의
val gradientBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFD1D5E9),
        Color(0xFF9DA6D0),
        Color(0xFF737FBC),
        Color(0xFF4B5BA9),
    )
)

// 각 기기를 표시하는 카드 UI
@Composable
fun DeviceCard(device: DevicesData,navController: NavHostController) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0x33FFFFFF), // 반투명 흰색 배경
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable {

                // 클릭 이벤트 처리
                when(device.deviceId.toInt()){

                    1->{
                        navController.navigate("light/${device.tagNumber}") {
                        popUpTo("splash") { inclusive = true }
                    }}
                    2->{
                        navController.navigate("speaker/${device.tagNumber}") {
                            popUpTo("splash") { inclusive = true }}
                    }
                    3 ->{
                        navController.navigate("minibig/${device.tagNumber}") {
                            popUpTo("splash") { inclusive = true }}
                    }
                    5->{
                        navController.navigate("airPurifier/${device.tagNumber}") {
                           }
                    }

                    else->{}
                }
            },
        border = BorderStroke(1.dp, gradientBrush) // 그라디언트 테두리
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // 아이콘 이미지
                Image(
                    painter = painterResource(id = R.drawable.ic_light),
                    contentDescription = "설명",
                    modifier = Modifier.size(25.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))

                // 기기 이름과 상태 텍스트
                Column (modifier = Modifier.weight(1f)){
                    Text(
                        text = device.deviceName,
                        fontSize = 13.sp,
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.padding(2.dp))
                    Text(
                        text = if (device.activated) "켜짐" else "꺼짐",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }

                // 우측 끝 설정 아이콘
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "설정",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// 리스트 중간에 구분선과 라벨 텍스트 표시용
@Composable
fun DividerWithLabel(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        Divider(modifier = Modifier.weight(1f), color = Color.Gray)
        Text(
            text = label,
            color = Color(0xFFD5D5D5),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 13.sp
        )
        Divider(modifier = Modifier.weight(1f), color = Color.Gray)
    }
    Spacer(modifier = Modifier.height(12.dp))
}

@Composable
fun WearIconButton(onClick: () -> Unit) {
    Image(
        painter = painterResource(id = R.drawable.ic_refresh),
        contentDescription = "My Image Button",
        modifier = Modifier
            .size(54.dp)
            .clickable(onClick = onClick).padding(top = 24.dp)
    )
}

// 프리뷰 (Wear OS 장치에서 시스템 UI 포함 화면 미리보기)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
//        DevicesScreen()
    }
}
