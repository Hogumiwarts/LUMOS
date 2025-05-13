package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.components.GlowingCard

@Composable
fun DeviceGridSection(
    devices: List<SmartThingsDevice>,
    selectedDeviceId: String? = null,
    onDeviceClick: (SmartThingsDevice) -> Unit = {}
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 0.dp,
            end = 0.dp,
            top = 25.dp,
            bottom = 25.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(devices) { index, device ->
            val isSelected = selectedDeviceId == device.deviceId
            val rows = (devices.size + 1) / 2
            val currentRow = index / 2

            val categoryName = device.components
                .firstOrNull()              // 첫 번째 컴포넌트
                ?.categories
                ?.firstOrNull()            // 그 컴포넌트의 첫 번째 카테고리
                ?.name                     // 카테고리 이름
                ?: "ETC"                   // 없으면 기본값

            val deviceType = DeviceListType.from(categoryName)
            val iconResId = deviceType.iconResId

            val cardContent: @Composable () -> Unit = {
                DeviceRoutineCard(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onDeviceClick(device) },
                    showToggle = false,
                    cardTitle = device.name,
                    cardSubtitle = if (device.isOn) "켜짐" else "꺼짐",
                    isOn = device.isOn,
                    iconSize = DpSize(85.dp, 85.dp),
                    cardIcon = { size ->
                        Image(
                            painter = painterResource(id = deviceType.iconResId),
                            contentDescription = null,
                            modifier = Modifier.size(size)
                        )

                    },
                    endPadding = 3.dp,
                    isActive = device.isActive
                )
            }

            Box(
                modifier = Modifier
                    .aspectRatio(1.05f)
            ) {
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
    }
}

@Preview(showBackground = true)
@Composable
fun DeviceGridSectionPreview() {
    val sampleDevices = listOf(
        MyDevice(1, "내 방 조명 1", isOn = true, isActive = true, deviceType = DeviceListType.LIGHT),
        MyDevice(
            2, "거실 공기청정기", isOn = false, isActive = true, deviceType = DeviceListType.AIR_CLEANER
        ),
        MyDevice(3, "무드 플레이어", isOn = true, isActive = false, deviceType = DeviceListType.SPEAKER),
        MyDevice(4, "침대 조명 스위치", isOn = false, isActive = true, deviceType = DeviceListType.SWITCH)
    )

    DeviceGridSection(
        devices = sampleDevices,
        selectedDeviceId = 2,
        onDeviceClick = { /* no-op for preview */ })
}
