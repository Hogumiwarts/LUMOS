package com.hogumiwarts.lumos.ui.screens.Routine.routineDeviceList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.ui.common.DeviceRoutineCard
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineDeviceListScreen(
    viewModel: RoutineDeviceListViewModel,
    devices: List<MyDevice>,
    onSelectComplete: () -> Unit,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // topBar
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "루틴 수정",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = nanum_square_neo
            )

        }

        Spacer(modifier = Modifier.height(20.dp))

        // 기기 목록
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(devices) { device ->
                DeviceRoutineCard(
                    modifier = Modifier
                        .aspectRatio(1.05f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            //todo: 파란색 shadow 적용하여 시각적으로 선택됨을 알림
                        },
                    showToggle = false, // 토글 X
                    cardTitle = device.deviceName,
                    cardSubtitle = if (device.isOn) "ON" else "OFF",
                    isOn = false,
                    iconSize = DpSize(85.dp, 85.dp),
                    cardIcon = { size ->
                        Image(
                            painter = painterResource(id = device.deviceType.iconResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(size)
                                //.offset(y = -2.dp)
                        )
                    },
                    endPadding = 3.dp,
                    isActive = device.isActive
                )
            }
        }

        // 선택 버튼
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
        navController = rememberNavController()
    )
}