package com.hogumiwarts.lumos.presentation.ui.screens.devices.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.domain.model.DeviceListData
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceType
import com.hogumiwarts.lumos.presentation.ui.screens.devices.gradientBrush

// 각 기기를 표시하는 카드 UI
@Composable
fun DeviceCard(device: DeviceListData, navController: NavHostController) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0x33FFFFFF), // 반투명 흰색 배경
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable {

                // 클릭 이벤트 처리
                when (DeviceType.fromId(device.deviceId.toInt())) {

                    DeviceType.LIGHT -> {
                        navController.navigate("light/${device.tagNumber}") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }

                    DeviceType.SPEAKER -> {
                        navController.navigate("speaker/${device.tagNumber}") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }

                    DeviceType.MINIBIG -> {
                        navController.navigate("minibig/${device.tagNumber}") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }

                    DeviceType.AIR_PURIFIER -> {
                        navController.navigate("airPurifier/${device.tagNumber}") {
                        }
                    }

                    else -> {}
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