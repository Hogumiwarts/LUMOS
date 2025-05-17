package com.hogumiwarts.lumos.ui.screens.control

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.uwb.RangingPosition
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.control.light.LightScreen
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FindDeviceScreen(
    navController: NavHostController,
    controlViewModel: ControlViewModel = hiltViewModel()
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.wave))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    val sessionReady = controlViewModel.sessionReady

    var showSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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

    // 화면에 들어왔을 떄 세션을 준비하고 레인징 시작
    LaunchedEffect(Unit) {
//        controlViewModel.prepareSession()
        controlViewModel.startSingleRanging()
    }

    LaunchedEffect(controlViewModel.detectedDeviceName) {
        // 기기를 탐지 하면 bottom sheet 열기 + 탐지를 중지
        if (controlViewModel.detectedDeviceName != null) {
            showSheet = true
            controlViewModel.cancelDetection()
        }
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

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxHeight()
                .padding(36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(0.2f))
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
//                    .clickable(
//                        interactionSource = remember { MutableInteractionSource() },
//                        indication = null,
//                        onClick = {
//                            if (sessionReady && !controlViewModel.rangingActive) {
////                                controlViewModel.startSingleRanging()
//                            }
//                        }
//                    )
            ) {
                if (controlViewModel.isDetecting) {
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier
                            .size(350.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.size(350.dp))
                }

                // TODO: 지팡이랑 로티 위치 확인하기(기기마다 다를수도)
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
            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "${controlViewModel.localAddress}",
                fontFamily = nanum_square_neo,
                fontSize = 11.sp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (controlViewModel.isDetecting) "연결 기기를 찾는 중..." else "지팡이를 눌러 탐지를 시작하세요.",
                style = MaterialTheme.typography.body1.copy(color = Color.White),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Row(
                Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
//                Button(
//                    onClick = {
//                        controlViewModel.startSingleRanging()
//                    },
//                    enabled = sessionReady && !controlViewModel.rangingActive
//                ) {
//                    Text("Ranging 시작")
//                }

                Button(
                    onClick = {
                        if (controlViewModel.rangingActive)
                            controlViewModel.startDetection()
                    },
                    enabled = controlViewModel.rangingActive
                ) {
                    Text("탐지 시작")
                }

                Button(onClick = { showSheet = true }) {
                    Text("시트")
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(0.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = "지금 가리키고 있는 기기를 찾고 있어요!\n연결할 기기를 향해 방향을 조절해 주세요.\n탐지 결과: ${controlViewModel.detectedDeviceName}",
                    style = MaterialTheme.typography.body1.copy(
                        color = Color.White,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
//            Spacer(modifier = Modifier.weight(0.4f))
        }


        // 방향 탐지 테스트 UI
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(bottom = 16.dp)
//        ) {
//            Column(
//                verticalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                controlViewModel.controleeAddresses.forEach { address ->
//                    val position = controlViewModel.getDevicePosition(address)
//                    val isConnected = position != null
//                    RangingResultText(
//                        address = address,
//                        isConnected = isConnected,
//                        position = position,
//                        findType = controlViewModel.detectedDeviceName
//                    )
//                }
//            }
//        }

    }

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
                    "공기청정기" -> Test1()
                    "조명" -> LightScreen()
                    "스피커" -> Test2()
                }
            }
        }
    }
}

@Composable
private fun Test1() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "공기청정기 화면")
    }
}

@Composable
private fun Test2() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "스피커 화면")
    }
}

@Composable
private fun RangingResultText(
    address: String,
    isConnected: Boolean,
    position: RangingPosition?,
    findType: String?
) {
    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "장치: $address",
                color = Color.White,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge
            )
            Text(
                text = if (isConnected) "연결됨" else "연결 안됨",
                color = if (isConnected) Color.Green else Color.Red
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (position != null) {
            // 거리 정보
            Row {
                Text(
                    text = "거리:",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = "${position.distance?.value ?: "N/A"} m",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }

            // 방위각 정보
            Row {
                Text(
                    text = "방위각:",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = "${position.azimuth?.value ?: "N/A"} °",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }

            // 고도 정보
            Row {
                Text(
                    text = "고도:",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.width(80.dp)
                )
                Text(
                    text = "${position.elevation?.value ?: "N/A"} °",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(12.dp))

            position.azimuth?.value?.let { azimuth ->
                ArrowIndicator(
                    azimuthDeg = azimuth * (-1),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Text(
                text = "탐지 결과: $findType",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                color = Color.Magenta,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        } else {
            Text(
                text = "데이터 없음",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FindDeviceScreenPreview() {
//    FindDeviceScreen()
}