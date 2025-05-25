package com.hogumiwarts.lumos.ui.screens.control

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.control.audio.SpeakerScreen
import com.hogumiwarts.lumos.ui.screens.control.light.LightScreen
import com.hogumiwarts.lumos.ui.screens.control.light.RealLightScreenContent
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectDeviceScreen(
    navController: NavHostController,
    controlViewModel: ControlViewModel
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.wave))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    var showSheet by remember { mutableStateOf(false) }

    // skipPartiallyExpanded: 중간 상태 생략 여부
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden }
    )

    val blockSheetDrag = remember {
        object : NestedScrollConnection {
            // 자식이 소비한 뒤 남은 스크롤
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset =
                // available.y 가 +↓든  –↑든 그대로 소비
                if (available.y != 0f) Offset(x = 0f, y = available.y)
                else Offset.Zero

            override suspend fun onPostFling(
                consumed: Velocity,
                available: Velocity
            ): Velocity =
                if (available.y != 0f) Velocity(x = 0f, y = available.y)
                else Velocity.Zero
        }
    }
    val absorbDrag = rememberDraggableState { }

    LaunchedEffect(Unit) {
        controlViewModel.startSingleRanging()
    }

    LaunchedEffect(controlViewModel.detectedDeviceName) {
        // 기기를 탐지 하면 bottom sheet 열기 + 탐지를 중지
        if (controlViewModel.detectedDeviceName != null) {
            showSheet = true
            controlViewModel.cancelDetection()
            controlViewModel.stopRanging()
        }
    }

    BackHandler {
        controlViewModel.cancelDetection()
        navController.popBackStack()
        controlViewModel.stopRanging()
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_loading),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // TopBar UI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .background(Color.Transparent)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(Color.Transparent)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = null,
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            // 뒤로 가기
                            navController.popBackStack()
                        }
                    ),
                    tint = Color.White
                )
            }
        }

        // 클릭 시 기기 방향 탐지 시작
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)
                .padding(top = 64.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        // 기기 방향 탐지 시작
                        if (controlViewModel.rangingActive) {
                            controlViewModel.startDetection()
                        }
                    }
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                LottieAnimation(
                    composition = if (controlViewModel.isDetecting) composition else null,
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
                Image(
                    painter = painterResource(id = R.drawable.wand),
                    contentDescription = null,
                    modifier = Modifier
                        .size(350.dp)
                        .align(Alignment.Center)
                        .offset(y = (130).dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(60.dp))

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (controlViewModel.isDetecting) "연결 기기를 찾는 중..." else "지팡이를 눌러 탐지를 시작하세요.",
                style = MaterialTheme.typography.body1.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "${controlViewModel.getDevicePosition("00:02")?.azimuth?.value ?: "N/A"} °",
                style = MaterialTheme.typography.body1.copy(color = Color.DarkGray),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 36.dp)
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .padding(vertical = 18.dp)
            ) {
                Text(
                    text = "지금 가리키고 있는 기기를 찾고 있어요!\n연결할 기기를 향해 방향을 조절해 주세요.",
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    // 바텀시트
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp),
            // 네비·제스처 바 뒤로 콘텐츠가 들어가도록 (선택 사항)
            windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Bottom),
            containerColor = Color.White,
            dragHandle = null,
        ) {
            // 시트 콘텐츠: 화면 높이만큼 채우기
            Box(
                Modifier
                    .nestedScroll(blockSheetDrag)
                    .draggable(
                        state = absorbDrag,
                        orientation = Orientation.Vertical,
                        startDragImmediately = false
                    )
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .statusBarsPadding()
            ) {
                when (controlViewModel.detectedDeviceName) {
                    "스위치" -> SwitchScreen(deviceId = 31)
                    "조명" -> RealLightScreenContent(deviceId = 18)
                    "스피커" -> SpeakerScreen(deviceId = 19)
                }
            }
        }
    }

}



@Preview(showBackground = true)
@Composable
fun DetectScreenPreview() {
//    DetectDeviceScreen()
}