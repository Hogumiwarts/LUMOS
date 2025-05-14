package com.hogumiwarts.lumos.ui.screens.devices

import android.content.Context
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toMyDevice
import com.hogumiwarts.lumos.ui.common.CommonDialog
import com.hogumiwarts.lumos.ui.common.CommonTopBar
import com.hogumiwarts.lumos.ui.common.DeviceGridSection
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun DeviceListScreen(
    viewModel: DeviceListViewModel = hiltViewModel(),
    onSelectedComplete: (MyDevice) -> Unit
) {
    val selectedDeviceId by viewModel.selectedDeviceId
    val showDialog by viewModel.showDialog

    val isLinked by viewModel.isLinked.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    val context = LocalContext.current

    val deviceList by viewModel.deviceList.collectAsState()

    // 화면이 다시 Resume되면 DB 저장 목록을 불러옴
    // 새로고침 클릭 & 계정 연동 시에만 SmartThings API 사용해서 새로 호출
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAccountLinked() // api 연동 확인되면 자동 기기 목록 불러오기
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
            onRightBtnClick = {
                // smartthings 계정 연동 이동
                //viewModel.fetchDevicesWithStatus() // 기기 목록 다시 불러옴
                viewModel.refreshDevicesFromDiscover(context)
            },
            rightIconResId = R.drawable.ic_refresh,
            barHeight = 20
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 28.dp)
        ) {

            if (!isLinked) {
                NotLinkedScreen(
                    onClickLink = {
                        viewModel.requestAuthAndOpen(context)
                    },
                    viewModel,
                    context
                )
            } else {
                val myDevices = deviceList.map { it }
                DeviceGridSection(
                    devices = myDevices,
                    selectedDeviceId = viewModel.getSelectedDevice(myDevices)?.deviceId,
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
fun NotLinkedScreen(onClickLink: () -> Unit, viewModel: DeviceListViewModel, context: Context) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
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
                        // smartthings 계정 연동 이동
                        viewModel.requestAuthAndOpen(context = context)
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
                    ),

                    )
            }

        }
    }
}

//@Preview(showBackground = true, widthDp = 360, heightDp = 800)
//@Composable
//fun DeviceListScreenPreview() {
//    // 가짜 ViewModel 생성
//    val fakeViewModel = object : DeviceListViewModel() {}
//
//    DeviceListScreen(
//        viewModel = fakeViewModel,
//        devices = MyDevice.sample,
//        onSelectedComplete = {}
//    )
//}
