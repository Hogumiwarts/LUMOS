package com.hogumiwarts.lumos.ui.screens.Routine.routineDeviceList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.ui.common.DeviceRoutineCard
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.Routine.components.GlowingCard
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineDeviceListScreen(
    viewModel: RoutineDeviceListViewModel,
    devices: List<MyDevice>,
    onSelectComplete: (MyDevice) -> Unit,
) {
    // 선택 기기 상태
    val selectedDeviceId by viewModel.selectedDeviceId
    val showDialog by viewModel.showDialog

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .statusBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // topBar
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "나의 기기 목록",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = nanum_square_neo
            )

        }


        // 기기 목록
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(devices) { index, device ->
                val isSelected = selectedDeviceId == device.deviceId

                // 전체 줄 수를 계산
                val rows = (devices.size + 1) / 2
                val currentRow = index / 2

                val topPadding = if (currentRow == 0) 20.dp else 0.dp
                val bottomPadding = if (currentRow == rows - 1) 20.dp else 0.dp

                val cardContent: @Composable () -> Unit = {
                    DeviceRoutineCard(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { // 특정 기기 클릭 시 동작
                                viewModel.onDeviceClicked(device)
                            },
                        showToggle = false,
                        cardTitle = device.deviceName,
                        cardSubtitle = if (device.isOn) "ON" else "OFF",
                        isOn = false,
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

                Box(
                    modifier = Modifier
                        .padding(top = topPadding, bottom = bottomPadding)
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
                                    1.dp,
                                    Color(0xFFE0E0E0),
                                    shape = RoundedCornerShape(10.dp)
                                )
                        ) {
                            cardContent()
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(125.dp))

        // 선택 버튼
        PrimaryButton(
            buttonText = "선택하기",
            onClick = {
                val selected = viewModel.getSelectedDevice(devices)
                if (selected != null) {
                    onSelectComplete(selected)
                }
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 다이얼로그 설정
        CustomDialog(showDialog, onDismiss = { viewModel.dismissDialog() })

    }

}

@Composable
fun CustomDialog(showDialog: Boolean, onDismiss: () -> Unit) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                PrimaryButton(
                    buttonText = "확인",
                    onClick = onDismiss
                )
            },
            title = {
                Text(
                    text = "선택할 수 없는 기기예요!",
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = "기기 상태가 비활성화로 감지되어 제어할 수 없습니다. " +
                            "거리가 멀어지면 비활성화로 전환될 수 있어요.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0x80151920),
                    textAlign = TextAlign.Center
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoutineDeviceListScreenPreview() {
    val fakeViewModel = remember { RoutineDeviceListViewModel() }

    RoutineDeviceListScreen(
        viewModel = fakeViewModel,
        devices = MyDevice.sample,
        onSelectComplete = {},
    )
}