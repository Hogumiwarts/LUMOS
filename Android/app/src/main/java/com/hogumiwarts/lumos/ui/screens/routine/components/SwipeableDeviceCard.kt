package com.hogumiwarts.lumos.ui.screens.routine.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hogumiwarts.domain.model.routine.CommandDevice

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableDeviceCard(
    device: CommandDevice,
    onDelete: () -> Unit
) {

    // 수정에서 카드 삭제 할 때 사용
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    // 스와이프해서 카드 삭제하기
    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            // 스와이프하면 보여줄 배경
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF26D6D), shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    "삭제",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy()
                )
            }
        },
        dismissContent = {
            DeviceCard(
                commandDevice = device, deviceType = DeviceListType.from(device.deviceType)
            )
        }
    )
}