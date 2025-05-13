package com.example.myapplication.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.example.myapplication.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun GestureTest(navController: NavController, activity: MainActivity, index: String) {
    // Lottie 애니메이션 리소스 로드
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_time))
    val lottieAnimatable = rememberLottieAnimatable()

    val context = LocalContext.current


    // 화면 상태 관리: "start", "animating", "end"
    var uiState by remember { mutableStateOf("start") }

    // 경과 시간 (ms 단위)
    var elapsedMillis by remember { mutableStateOf(0L) }

    // 타이머 실행 여부
    var isTimerRunning by remember { mutableStateOf(false) }

    // 테스트 완료 여부
    var isTestFinished by remember { mutableStateOf(false) }

    var count by remember { mutableStateOf(4) }

    // 코루틴 스코프 (Button 클릭 등에서 사용)
    val scope = rememberCoroutineScope()

    // 애니메이션 시작 시 Lottie 실행
    LaunchedEffect(uiState, composition) {
        if (uiState == "animating" && composition != null) {
            lottieAnimatable.animate(
                composition,
                iterations = 1 // 1회만 재생
            )
            uiState = "end"  // 애니메이션 끝나면 테스트 화면으로 이동
            activity.toggleIMU()
            isTimerRunning = true // 타이머 시작
        }
    }

    // 경과 시간 측정 로직
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (!isTestFinished) {
                delay(10L) // 10ms 단위로 시간 추가
                elapsedMillis += 10L
            }
        }
    }

    // 화면 전체를 감싸는 레이아웃
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            "start" -> { // 처음 시작 화면

                StartScreen({uiState = "animating"})
            }
            "animating" -> { // 애니메이션 재생 화면

                AnimatingScreen(
                    composition = composition,
                    lottieAnimatable = lottieAnimatable
                )
            }
            "end" -> {

                EndScreen(
                    isTestFinished = isTestFinished,
                    elapsedMillis = elapsedMillis,
                    onTestFinish = {
                        isTestFinished = true
                        activity.toggleIMU()

                                   },
                    onRestart = {
                        uiState = "start"
                        elapsedMillis = 0L
                        isTimerRunning = false
                        isTestFinished = false
                    },
                    onNext = {
                        activity.saveAndSendIMUData("$index",count)
                        navController.navigate("gesture_result"){
                            popUpTo("gesture_screen")
                        }
                    },
                    onInference = {
                        activity.sendIMUData("$index",count)
                        navController.navigate("gesture_result"){
                            popUpTo("gesture_screen")
                        }
                    },
                    scope = scope,
                    lottieAnimatable = lottieAnimatable
                    ,activity,
                    count,
                    onCountChange = { count = it }
                )
            }
        }
    }
}


@Composable
fun StartScreen(
    onStartClick: () -> Unit
) {
    Button(
        onClick = onStartClick,
        colors = ButtonDefaults.buttonColors(Color.Gray),
        modifier = Modifier.size(100.dp)
    ) {
        Text(text = "테스트 시작", color = Color.White)
    }
}

@Composable
fun AnimatingScreen(
    composition: LottieComposition?,
    lottieAnimatable: LottieAnimatable
) {
    if (composition != null) {
        LottieAnimation(
            composition = composition,
            progress = { lottieAnimatable.progress },
            modifier = Modifier.fillMaxSize()
        )
    }
}





@Composable
fun EndScreen(
    isTestFinished: Boolean,
    elapsedMillis: Long,
    onTestFinish: () -> Unit,
    onRestart: () -> Unit,
    onNext: () -> Unit,
    onInference: () -> Unit,
    scope: CoroutineScope,
    lottieAnimatable: LottieAnimatable,
    activity: MainActivity,
    count: Int,
    onCountChange: (Int) -> Unit
) {


    if (!isTestFinished) {
        // 테스트 중 화면


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "테스트 중",
                color = Color.White,
                modifier = Modifier.padding(top = 30.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatElapsedTime(elapsedMillis),
                color = Color.White,
                fontSize = 30.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onTestFinish,
                colors = ButtonDefaults.buttonColors(Color.Gray),
                shape = RoundedCornerShape(topStartPercent = 0, topEndPercent = 0),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "테스트 종료", color = Color.White)
            }


        }
    } else {
        // 테스트 종료 후 화면
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "테스트 종료",
                color = Color.White,
                fontSize = 20.sp,
            )

            Spacer(modifier = Modifier.weight(1f))

            Column (horizontalAlignment = Alignment.CenterHorizontally){

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = {
                            if (count > 0) onCountChange(count - 1)
                        },
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "-", color = Color.White)
                    }

                    Text(
                        text = count.toString(),
                        fontSize = 20.sp,
                        color = Color.White // 색상을 명시해 주세요
                    )
                    Button(
                        onClick = {
                            onCountChange(count + 1)
                        },
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "+", color = Color.White)
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                lottieAnimatable.snapTo(progress = 1f)
                                onRestart()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "재실행", color = Color.White)
                    }

                    Button(
                        onClick = onNext,
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = "저장", color = Color.White)
                    }
                }

                Button(
                    onClick = onInference,
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text(text = "추론", color = Color.White)
                }
            }


            Spacer(modifier = Modifier.weight(2f))
        }
    }
}

// 시간 포맷 함수도 따로 utils에 빼면 더 좋아!
fun formatElapsedTime(millis: Long): String {
    val seconds = (millis / 1000)
    val millisPart = (millis % 1000) / 10
    return String.format("%02d.%02d초", seconds, millisPart)
}


// Wear OS 화면 프리뷰 (System UI 포함)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewGestureTest() {
    MaterialTheme {
        // Preview에서 navController 없어서 주석 처리
//        GestureTest()
    }
}
