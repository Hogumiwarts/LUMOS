package com.hogumiwarts.lumos.ui.screens.Devices

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.CommonDialog
import com.hogumiwarts.lumos.ui.common.CommonTopBar
import com.hogumiwarts.lumos.ui.common.DeviceGridSection
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun DeviceListScreen(
    viewModel: DeviceListViewModel = hiltViewModel(),
    devices: List<MyDevice>,
    onSelectedComplete: (MyDevice) -> Unit
) {
    val selectedDeviceId by viewModel.selectedDeviceId
    val showDialog by viewModel.showDialog

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding()
            .padding(top = 24.dp)
    ) {

        // topBar
        CommonTopBar(
            barTitle = "나의 기기 목록",
            onBackClick = { /*TODO*/ },
            isRightBtnVisible = true,
            onRightBtnClick = { /*TODO*/ },
            rightIconResId = R.drawable.ic_refresh,
            barHeight = 20
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp)
        ) {
            DeviceGridSection(
                devices = devices,
                selectedDeviceId = selectedDeviceId,
                onDeviceClick = { viewModel.onDeviceClicked(it) }
            )
        }
    }

    CommonDialog(
        showDialog = showDialog,
        onDismiss = { viewModel.dismissDialog() },
        titleText = "선택할 수 없는 기기예요!",
        bodyText = "기기 상태가 비활성화로 감지되어 제어할 수 없습니다. 거리가 멀어지면 비활성화로 전환될 수 있어요."
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DeviceListScreenPreview() {
    // 가짜 ViewModel 생성
    val fakeViewModel = object : DeviceListViewModel() {}

    DeviceListScreen(
        viewModel = fakeViewModel,
        devices = MyDevice.sample,
        onSelectedComplete = {}
    )
}
