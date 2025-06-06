package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.hogumiwarts.data.entity.remote.Response.SmartThingsDevice
import com.hogumiwarts.lumos.mapper.toMyDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.components.GlowingCard

@Composable
fun DeviceGridSection(
    devices: List<MyDevice>,
    selectedDeviceId: Long? = null,
    onDeviceClick: (MyDevice) -> Unit = {},
    showToggle: Boolean = true
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 2개씩 묶어서 Row로 배치
        devices.chunked(2).forEach { rowDevices ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                rowDevices.forEach { device ->
                    val isSelected = selectedDeviceId == device.deviceId

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.05f)
                    ) {
                        val cardContent: @Composable () -> Unit = {
                            DeviceRoutineCard(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable { onDeviceClick(device) },
                                showToggle = showToggle,
                                cardTitle = device.deviceName,
                                cardSubtitle = if (device.isOn) "켜짐" else "꺼짐",
                                isOn = device.isOn,
                                iconSize = DpSize(85.dp, 85.dp),
                                cardIcon = { size ->
                                    Image(
                                        painter = painterResource(id = device.deviceType.iconResId),
                                        contentDescription = null,
                                        modifier = Modifier.size(size)
                                    )
                                },
                                endPadding = 3.dp,
                                isActive = device.isActive
                            )
                        }

                        if (isSelected) {
                            GlowingCard(
                                modifier = Modifier
                                    .aspectRatio(1.05f)
                                    .fillMaxSize(),
                                glowingColor = Color(0xFF3D5AFE),
                                containerColor = Color.White,
                                cornerRadius = 12.dp,
                                glowingRadius = 24.dp
                            ) {
                                cardContent()
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1.05f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(
                                        1.dp, Color(0xFFE0E0E0), shape = RoundedCornerShape(10.dp)
                                    )
                            ) {
                                cardContent()
                            }
                        }
                    }
                }

                // 홀수 개의 디바이스가 있을 때 빈 공간 채우기
                if (rowDevices.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DeviceGridSectionPreview() {
//    val sampleDevices = listOf(
//        MyDevice(1, "내 방 조명 1", isOn = true, isActive = true, deviceType = DeviceListType.LIGHT),
//        MyDevice(
//            2, "거실 공기청정기", isOn = false, isActive = true, deviceType = DeviceListType.AIR_CLEANER
//        ),
//        MyDevice(3, "무드 플레이어", isOn = true, isActive = false, deviceType = DeviceListType.SPEAKER),
//        MyDevice(4, "침대 조명 스위치", isOn = false, isActive = true, deviceType = DeviceListType.SWITCH)
//    )
//
//    DeviceGridSection(
//        devices = sampleDevices,
//        selectedDeviceId = 2,
//        onDeviceClick = { /* no-op for preview */ })
//}
