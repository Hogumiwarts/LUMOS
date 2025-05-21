package com.hogumiwarts.lumos.ui.screens.control

import androidx.core.uwb.RangingPosition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun MultiRangingScreen(
//    navController: NavHostController,
    controlViewModel: ControlViewModel = hiltViewModel()
) {
    // 코루틴 스코프 생성
    val coroutineScope = rememberCoroutineScope()

    // 레인징 상태 추적
    val rangingActive = controlViewModel.multiRangingActive
    val rangingPositions = controlViewModel.multiRangingPositions
    val controleeAddresses = remember { listOf("00:01", "00:02") }

    // UI 업데이트를 위한 상태
    var refreshTrigger by remember { mutableStateOf(0) }

    val pstsKey by controlViewModel.pstsKeyHex.collectAsState()

    // 주기적 UI 업데이트
    LaunchedEffect(refreshTrigger) {
        delay(200)  // 200ms 마다 UI 업데이트
        refreshTrigger = (refreshTrigger + 1) % 1000
    }

    LaunchedEffect(Unit) {
        controlViewModel.generatePstsKey()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF202E70))
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(64.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
//            IconButton(
//                onClick = { navController.popBackStack() }
//            ) {
//                Icon(
//                    imageVector = Icons.Default.ArrowBack,
//                    contentDescription = "뒤로 가기",
//                    tint = Color.White
//                )
//            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "멀티 UWB 레인징",
                style = MaterialTheme.typography.h6.copy(
                    color = Color.White,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // 메인 콘텐츠
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 로컬 디바이스 정보
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.9f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "내 디바이스",
                        style = MaterialTheme.typography.subtitle1.copy(
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "주소: ${controlViewModel.localAddress}",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "세션 준비 상태: ${if (controlViewModel.sessionReady) "준비됨" else "준비 중..."}",
                        style = MaterialTheme.typography.body2.copy(
                            fontFamily = nanum_square_neo,
                            color = if (controlViewModel.sessionReady) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                    )
                    if (pstsKey != null) {
                        Text(
                            text = "P-STS 키: $pstsKey",
                            color = Color.Blue
                        )
                    } else {
                        Text(
                            text = "P-STS 키 아직 생성 안 됨",
                            color = Color.Gray
                        )
                    }
                }
            }

            // 레인징 제어 버튼
            Button(
                onClick = {
                    if (rangingActive) {
                        coroutineScope.launch {
                            controlViewModel.stopMultiRanging()
                        }
                    } else {
                        controlViewModel.startMultiRanging()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (rangingActive) Color(0xFFE57373) else Color(0xFF4CAF50)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (rangingActive) "레인징 중지" else "레인징 시작",
                    style = MaterialTheme.typography.button.copy(
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
            }

            // 레인징 상태 카드
            Text(
                text = "연결된 디바이스: ${rangingPositions.size}",
                style = MaterialTheme.typography.h6.copy(
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            // 각 디바이스 레인징 결과 카드
            controleeAddresses.forEach { address ->
                DeviceRangingCard(
                    address = address,
                    isConnected = rangingPositions.containsKey(address),
                    rangingPosition = controlViewModel.getMultiDevicePosition(address)
                )
            }

            // 디버그 정보
            if (rangingActive) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF263238).copy(alpha = 0.8f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "디버그 정보",
                            style = MaterialTheme.typography.subtitle1.copy(
                                fontFamily = nanum_square_neo,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "총 연결 수: ${rangingPositions.size}",
                            style = MaterialTheme.typography.body2.copy(
                                fontFamily = nanum_square_neo,
                                color = Color.White
                            )
                        )

                        Text(
                            text = "활성 주소: ${rangingPositions.keys.joinToString()}",
                            style = MaterialTheme.typography.body2.copy(
                                fontFamily = nanum_square_neo,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceRangingCard(
    address: String,
    isConnected: Boolean,
    rangingPosition: RangingPosition?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isConnected) Color.White else Color.White.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "디바이스: $address",
                    style = MaterialTheme.typography.h6.copy(
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )

                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFFF5722),
                            shape = RoundedCornerShape(6.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "상태: ${if (isConnected) "연결됨" else "연결 안됨"}",
                style = MaterialTheme.typography.body1.copy(
                    fontFamily = nanum_square_neo,
                    color = if (isConnected) Color(0xFF4CAF50) else Color(0xFFFF5722)
                )
            )

            if (isConnected && rangingPosition != null) {
                Spacer(modifier = Modifier.height(16.dp))

                // 거리 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "거리: ",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "${rangingPosition.distance?.value?.let { "%.2f m" } ?: "측정 중..."}",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 방위각 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "방위각: ",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "${rangingPosition.azimuth?.value?.let { "%.1f°" } ?: "측정 중..."}",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 고도각 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "고도각: ",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "${rangingPosition.elevation?.value?.let { "%.1f°" } ?: "측정 중..."}",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 측정 시간 정보
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "측정 시간: ",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Text(
                        text = "${rangingPosition.elapsedRealtimeNanos / 1_000_000} ms 전",
                        style = MaterialTheme.typography.body1.copy(
                            fontFamily = nanum_square_neo
                        )
                    )
                }
            } else if (isConnected) {
                Text(
                    text = "레인징 데이터를 가져오는 중...",
                    style = MaterialTheme.typography.body2.copy(
                        fontFamily = nanum_square_neo,
                        color = Color(0xFF9E9E9E)
                    )
                )
            }
        }
    }
}