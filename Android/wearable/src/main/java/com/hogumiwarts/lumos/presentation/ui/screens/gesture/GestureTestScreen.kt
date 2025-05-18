package com.hogumiwarts.lumos.presentation.ui.screens.gesture

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.viewmodel.WebSocketViewModel
import com.hogumiwarts.lumos.service.SensorService
import org.json.JSONObject
import kotlin.math.log

@Composable
fun GestureTestScreen(
    type: String,
    isMeasuring: Boolean,
    prediction: String,
    onToggleMeasurement: () -> Unit,
    onFinish: (String) -> Unit,
) {

    val context = LocalContext.current
    val webSocketViewModel: WebSocketViewModel = viewModel()


    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as android.os.Vibrator
        }
    }

    // 제스처 이미지
    val gestureUrl = remember(type) {
        try {
            JSONObject(type).optString("gestureUrl")
        } catch (e: Exception) {
            null
        }
    }

    // 제스처 아이디
    val gestureId = remember(type) {
        try {
            JSONObject(type).optString("gestureId")
        } catch (e: Exception) {
            null
        }
    }

    var isCompleted by remember { mutableStateOf(false) }
    var lastKnownPrediction by remember { mutableStateOf("예측 없음") }
    var isShowingFailure by remember { mutableStateOf(false) } // 실패 상태 표시용

    val message = (prediction == gestureId)
    Log.d("TAG", "제스처 예측: $prediction $gestureId")


    // 인식 완료 시 WebSocket 연결 끊기
    LaunchedEffect(message) {
        if (message && !isCompleted) {
            Log.d("GestureTestScreen", "제스처 인식 완료 - WebSocket 연결 끊기")

            isCompleted = true  // 완료 상태로 설정
            lastKnownPrediction = prediction  // 마지막 예측 저장

            // 강한 성공 진동
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                    android.os.VibrationEffect.createOneShot(
                        500,
                        android.os.VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(500)
            }

            webSocketViewModel.disconnectWebSocket()
            onFinish("done")
        }
    }

    // 인식 실패 시 햅틱 진동
    LaunchedEffect(prediction) {
        if (!isCompleted && prediction != lastKnownPrediction) {
            // 예측이 실패 케이스인 경우
            if (prediction != "0" && prediction != "5" && prediction != "6" &&
                prediction != gestureId && prediction != "예측 없음"
            ) {

                Log.d("GestureTestScreen", "제스처 인식 실패 - 햅틱 진동")

                // 실패 상태 표시
                isShowingFailure = true

                // 강한 실패 진동 (2번 짧게)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 100, 100, 100)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    vibrator.vibrate(
                        android.os.VibrationEffect.createWaveform(timings, amplitudes, -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 100, 100, 100), -1)
                }

                // 2초 후 실패 상태 해제
                kotlinx.coroutines.delay(2000)
                isShowingFailure = false
            }
            lastKnownPrediction = prediction
        }
    }

    // 서비스 시작 (센서 수집 및 추론 루프 실행)
    LaunchedEffect(Unit) {
        val intent = Intent(context, SensorService::class.java)
        intent.putExtra("isTest", true)
        ContextCompat.startForegroundService(context, intent)

        // MainActivity에서 이미 시작했지만, 혹시 시작되지 않았다면 시작
        if (!isMeasuring) {
            onToggleMeasurement()
            Log.d("GestureTestScreen", "LaunchedEffect: 측정 시작")
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            // 측정 중인 경우 중지
            if (isMeasuring) {
                onToggleMeasurement()
            }

            webSocketViewModel.disconnectWebSocket()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1021)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 제스처 인식 결과
            if (isCompleted || message) {
                Text(
                    text = "인식 완료",
                    fontSize = 11.sp,
                    color = Color.Green
                )

            } else if (isShowingFailure) {
                Text(
                    text = "인식 실패",
                    fontSize = 11.sp,
                    color = Color.Red
                )
            } else {
                Text(
                    text = "인식 중...",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }

            Spacer(modifier = Modifier.size(8.dp))
            // 안내 문구
            Text(
                text = if (isCompleted) "제스처를 완료했습니다!" else "제스처를 실행해보세요.",
                fontSize = 15.sp,
                color = Color.White
            )

            AsyncImage(
                model = gestureUrl,
                contentDescription = "제스처 이미지",
                modifier = Modifier.size(80.dp)
            )

            // 완료 버튼 (인식 완료 시 보이지 않음)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF3A3A3C))
                    .clickable(enabled = isCompleted) {
                        onFinish(if (isCompleted) "done" else "fail")
                        (context as? Activity)?.finish()
                    }
                    .padding(horizontal = 33.dp, vertical = 11.dp)
            ) {
                Text(
                    text = "완료하기" ,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }

        }
    }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
//        GestureTestScreen({})
    }
}