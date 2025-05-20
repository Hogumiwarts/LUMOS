package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.routine.components.GlowingCard
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun DeviceGridHomeSection(
    devices: List<MyDevice>,
    selectedDeviceId: Long? = null,
    onDeviceClick: (MyDevice) -> Unit = {},
    onToggleDevice: (MyDevice) -> Unit = {}
) {
    Column {
        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = "현재 작동 상태",
            style = TextStyle(
                fontSize = 18.sp,
                lineHeight = 35.sp,
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight(800),
                color = Color(0xFF000000),
                letterSpacing = 0.4.sp,
            )
        )

        Spacer(modifier = Modifier.height(6.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 0.dp,
                end = 0.dp,
                top = 15.dp,
                bottom = 40.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(devices) { index, device ->
                val isSelected = selectedDeviceId == device.deviceId
                val rows = (devices.size + 1) / 2
                val currentRow = index / 2

                val cardContent: @Composable () -> Unit = {
                    DeviceRoutineCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                onDeviceClick(device)
                            },
                        showToggle = true,
                        cardTitle = device.deviceName,
                        cardSubtitle = device.deviceType.categoryName,
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
                        isActive = device.isActive,
                        onToggle = {
                            onToggleDevice(device)
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .aspectRatio(1.05f)
                ) {

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

