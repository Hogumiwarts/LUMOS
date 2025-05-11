package com.hogumiwarts.lumos.ui.screens.Devices

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun DeviceListScreen(
    viewModel: DeviceListViewModel = hiltViewModel(),
    devices: List<MyDevice>,
    onSelectedComplete: (MyDevice) -> Unit
) {
    val selectedDeviceId by viewModel.selectedDeviceId
    val showDialog by viewModel.showDialog

    val isLinked by viewModel.isLinked.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.checkAccountLinked()
    }

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

            if (!isLinked) {
                NotLinkedScreen(
                    onClickLink = {/* todo: smartThings 연동 창으로 이동 */ },
                    viewModel
                )
            } else {
                DeviceGridSection(
                    devices = devices,
                    selectedDeviceId = selectedDeviceId,
                    onDeviceClick = { viewModel.onDeviceClicked(it) }
                )
            }
        }
    }

    CommonDialog(
        showDialog = showDialog,
        onDismiss = { viewModel.dismissDialog() },
        titleText = "선택할 수 없는 기기예요!",
        bodyText = "기기 상태가 비활성화로 감지되어 제어할 수 없습니다. 거리가 멀어지면 비활성화로 전환될 수 있어요."
    )
}

@Composable
fun NotLinkedScreen(onClickLink: () -> Unit, viewModel: DeviceListViewModel) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
            .padding(bottom = 80.dp), // 네비게이션 높이 제외
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_alert_bubble),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(17.dp))

            Text(
                text = "SmartThings 계정이 아직 연동되지 않았어요.\n기기를 불러오기 위해 먼저 계정을 연동해주세요!",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF606060),
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .background(color = Color(0x1A1A1C3A), shape = RoundedCornerShape(size = 10.dp))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .clickable {
                        // todo: SmartThings 계정 
                        
                        // 연동 끝나면 상태 업데이트
                        viewModel.checkAccountLinked()
                    }
            ) {
                Text(
                    text = "계정 연동하고 기기 불러오기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF4B5BA9),

                        textAlign = TextAlign.Center,
                    )
                )
            }

        }
    }
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
