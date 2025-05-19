package com.hogumiwarts.lumos.presentation.ui.screens.devices.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScalingLazyColumnState
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.viewmodel.WebSocketViewModel


@Composable
@OptIn(ExperimentalHorologistApi::class)
fun LoadedDevice(
    devices: List<com.hogumiwarts.domain.model.devices.DeviceListData>,
    listState: ScalingLazyColumnState,
    navController: NavHostController,
) {

    // WebSocketViewModel에서 제스처 인식 상태 관찰
    val webSocketViewModel: WebSocketViewModel = hiltViewModel()
    val recognitionMode by webSocketViewModel.recognitionMode.collectAsState()

    // 제스처 인식 활성화 상태 확인
    val isGestureActive = recognitionMode == WebSocketViewModel.GestureRecognitionMode.ACTIVE ||
            recognitionMode == WebSocketViewModel.GestureRecognitionMode.ACTIVATING


    // 활성화 여부에 따라 리스트 분리
    val (activatedDevices, deactivatedDevices) = devices.partition { it.activated }

    ScreenScaffold(
        scrollState = listState,
    ) {
        // 배경 이미지와 전체 레이아웃 박스
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = R.drawable.device_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )


            // 스크롤 가능한 리스트
            ScalingLazyColumn(
                columnState = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
            ) {
                // 상단 타이틀 + 활성화 영역 구분선

                item {
                    Row(modifier = Modifier) {
                        Text(
                            text = "나의 기기 목록",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,

                            )

                        GestureActiveIndicator(
                            isActive = isGestureActive,
                            isActivating = recognitionMode == WebSocketViewModel.GestureRecognitionMode.ACTIVATING
                        )
                    }
                }

                item {
                    DividerWithLabel("활성화")
                }

                // 활성화된 기기 리스트 렌더링
                items(activatedDevices) {
                    DeviceCard(it, navController)
                }

                // 비활성화 영역 구분선
                item {
                    DividerWithLabel("비활성화")
                }

                // 비활성화된 기기 리스트 렌더링
                items(deactivatedDevices) {
                    DeviceCard(it, navController)
                }

                // 마지막 여백
                item {
                    WearIconButton()
                }
            }
        }
    }
}

@Composable
fun GestureActiveIndicator(
    isActive: Boolean,
    isActivating: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(12.dp)
    ) {
        Canvas(
            modifier = Modifier.size(12.dp)
        ) {
            val color = when {
                isActive -> Color.Green
                isActivating -> Color.Yellow
                else -> Color.Gray
            }

            // 외부 원
            drawCircle(
                color = color.copy(alpha = 0.3f),
                radius = size.minDimension / 2f
            )

            // 내부 원
            drawCircle(
                color = color,
                radius = size.minDimension / 3f
            )
        }
    }
}