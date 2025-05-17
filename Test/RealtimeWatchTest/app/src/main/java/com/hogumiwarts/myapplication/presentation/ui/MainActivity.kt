package com.hogumiwarts.myapplication.presentation.ui
// MainActivity.kt

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.hogumiwarts.myapplication.data.model.GestureData
import com.hogumiwarts.myapplication.presentation.viewmodel.GestureViewModel
import com.hogumiwarts.myapplication.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var linearAccelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private val imuDataList = mutableListOf<GestureData>()

    private var tempLINEARAccel: FloatArray? = null
    private var tempAccel: FloatArray? = null
    private var tempGyro: FloatArray? = null
    private var lastTimestamp: Long = 0

    private val gestureViewModel: GestureViewModel by viewModels()

    private val SLIDING_WINDOW_SIZE = 50
    private val SLIDING_STEP = 5
    private val SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME

    private var isPauseActive = false  // 일시 정지 상태 추적을 위한 변수

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // 센서 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // 진동 초기화
        gestureViewModel.initVibrator(this)

        // 웹소켓 연결
        gestureViewModel.connectWebSocket()

        // 라이프사이클 옵저버 등록
        lifecycle.addObserver(gestureViewModel)

        // 앱 시작 시 센서 시작
        startSensors()
        Log.d("MainActivity", "앱 시작: 센서 활성화")

        setContent {
            val prediction by gestureViewModel.prediction
            val history = gestureViewModel.history
            val uiState by gestureViewModel.uiState.collectAsState()
            val isActive by gestureViewModel.recognitionMode.collectAsState()

            // 제스처 인식 상태 변경 감지
            DisposableEffect(isActive) {
                Log.d("MainActivity", "제스처 인식 상태 변경: $isActive")
                onDispose { }
            }

            WearApp(
                isActive = isActive != GestureViewModel.GestureRecognitionMode.INACTIVE,
                prediction = prediction,
                history = history,
                uiState = uiState,
                onToggleMeasurement = {
                    gestureViewModel.toggleGestureRecognition()
                }
            )
        }

        // 제스처 콜백 설정
        gestureViewModel.onGesture1Detected = {
            Log.d("Gesture", "제스처 1 감지됨: 활성화/비활성화 토글")
        }

        gestureViewModel.onGesture2Detected = {
            Log.d("Gesture", "제스처 2 감지됨: 박수 두 번")
            clearIMUDataAndPauseCollection()
        }

        gestureViewModel.onGesture3Detected = {
            Log.d("Gesture", "제스처 3 감지됨: 손바닥 왼쪽 스와이프")
            clearIMUDataAndPauseCollection()
        }
    }

    // 센서 시작 함수
    private fun startSensors() {
        // 기존 리스너 모두 해제
        sensorManager.unregisterListener(this)
        imuDataList.clear()

//        Log.d("Sensors", "센서 시작: 일반 모드")

        // 모든 센서를 게임용 샘플링 레이트로 등록
        val accResult = sensorManager.registerListener(this, accelerometer, SENSOR_DELAY)
        val linearAccResult = sensorManager.registerListener(this, linearAccelerometer, SENSOR_DELAY)
        val gyroResult = sensorManager.registerListener(this, gyroscope, SENSOR_DELAY)

        // 등록 결과 로깅
//        Log.d("Sensors", "센서 등록 결과 - 가속도: $accResult, 자이로: $gyroResult, 선형가속도: $linearAccResult")
    }

    // 센서 중지 함수
    private fun stopSensors() {
        sensorManager.unregisterListener(this)
        imuDataList.clear()
        Log.d("Sensors", "센서 중지")
    }

    // 데이터 수집 일시 정지
    private fun clearIMUDataAndPauseCollection() {
        // 버퍼 지우기
        imuDataList.clear()

        // 일시 정지 상태 설정
        isPauseActive = true

        // 짧은 시간 동안 측정 일시 중지 (연속 감지 방지)
        Handler(Looper.getMainLooper()).postDelayed({
            // 일시 정지 상태 해제
            isPauseActive = false

            // 활동 알림 (타이머 리셋)
            if (gestureViewModel.isGestureRecognitionActive()) {
                gestureViewModel.notifyActivity()
            }
        }, 2000)  // 2000ms = 2초
    }

    override fun onResume() {
        super.onResume()
        // 앱이 화면에 보일 때 센서 다시 시작
        startSensors()
    }

    override fun onPause() {
        super.onPause()
        // 앱이 화면에서 사라질 때 센서 중지 (배터리 절약)
        stopSensors()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        // 일시 정지 상태나 이벤트가 null이면 처리하지 않음
        if (isPauseActive || event == null) return

        // 센서 데이터 수집
        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                tempLINEARAccel = event.values.clone()
//                Log.d("SensorData", "선형가속도: ${event.values.contentToString()}")
            }
            Sensor.TYPE_ACCELEROMETER -> {
                tempAccel = event.values.clone()
//                Log.d("SensorData", "가속도계: ${event.values.contentToString()}")
            }
            Sensor.TYPE_GYROSCOPE -> {
                tempGyro = event.values.clone()
//                Log.d("SensorData", "자이로스코프: ${event.values.contentToString()}")
            }
        }

        lastTimestamp = System.currentTimeMillis()

        // 데이터 저장
        trySaveIMUData()

        // 제스처 인식 상태에 따른 처리
        if (imuDataList.size >= SLIDING_WINDOW_SIZE) {
            if (gestureViewModel.isGestureRecognitionActive()) {
                // 활성화 상태: 모든 제스처 인식
                trySendNormalData()
                gestureViewModel.notifyActivity()
            } else {
                // 비활성화 상태: 제스처 1(활성화 토글용)만 인식
                trySendActivationOnlyData()
            }
        }
    }

    // 센서 데이터 저장
    private fun trySaveIMUData() {
        if (tempAccel != null && tempGyro != null && tempLINEARAccel != null) {
            val (ax, ay, az) = tempAccel!!
            val (gx, gy, gz) = tempGyro!!
            val (lax, lay, laz) = tempLINEARAccel!!

            imuDataList.add(
                GestureData(
                    timestamp = lastTimestamp,
                    accX = ax, accY = ay, accZ = az,
                    liAccX = lax, liAccY = lay, liAccZ = laz,
                    gryoX = gx, gryoY = gy, gryoZ = gz
                )
            )

            // 데이터 저장 로그
            if (imuDataList.size % 10 == 0) {
//                Log.d("SensorData", "데이터 저장: 목록 크기=${imuDataList.size}")
            }

            // 초기화
            tempAccel = null
            tempGyro = null
            tempLINEARAccel = null
        }
    }

    // 일반 모드: 모든 제스처 인식
    private fun trySendNormalData() {
        if (imuDataList.size >= SLIDING_WINDOW_SIZE) {
            val window = imuDataList.takeLast(SLIDING_WINDOW_SIZE)

            if (window.size != SLIDING_WINDOW_SIZE) {
                Log.e("WebSocket", "❌ 슬라이딩 윈도우 크기가 맞지 않습니다!")
                return
            }

            val json = JSONObject().apply {
                put("gesture_id", 0)
                put("data", JSONArray().apply {
                    window.forEach {
                        put(JSONObject().apply {
                            put("timestamp", it.timestamp)
                            put("acc_x", it.accX)
                            put("acc_y", it.accY)
                            put("acc_z", it.accZ)
                            put("li_acc_x", it.liAccX)
                            put("li_acc_y", it.liAccY)
                            put("li_acc_z", it.liAccZ)
                            put("gryo_x", it.gryoX)
                            put("gryo_y", it.gryoY)
                            put("gryo_z", it.gryoZ)
                        })
                    }
                })
            }

//            Log.d("WebSocket", "일반 모드 데이터 전송: 윈도우 크기=${window.size}")
            gestureViewModel.sendIMUData(json.toString())
            imuDataList.removeAll(imuDataList.take(SLIDING_STEP))
        }
    }

    // 활성화 전용 모드: 제스처 1만 인식
    private fun trySendActivationOnlyData() {
        if (imuDataList.size >= SLIDING_WINDOW_SIZE) {
            val window = imuDataList.takeLast(SLIDING_WINDOW_SIZE)

            if (window.size != SLIDING_WINDOW_SIZE) {
                Log.e("WebSocket", "❌ 슬라이딩 윈도우 크기가 맞지 않습니다!")
                return
            }

            val json = JSONObject().apply {
                put("gesture_id", 0)
                put("mode", "activation_only")  // 서버에 활성화 모드임을 알림
                put("data", JSONArray().apply {
                    window.forEach {
                        put(JSONObject().apply {
                            put("timestamp", it.timestamp)
                            put("acc_x", it.accX)
                            put("acc_y", it.accY)
                            put("acc_z", it.accZ)
                            put("li_acc_x", it.liAccX)
                            put("li_acc_y", it.liAccY)
                            put("li_acc_z", it.liAccZ)
                            put("gryo_x", it.gryoX)
                            put("gryo_y", it.gryoY)
                            put("gryo_z", it.gryoZ)
                        })
                    }
                })
            }

//            Log.d("WebSocket", "활성화 전용 모드 데이터 전송: 윈도우 크기=${window.size}")
            gestureViewModel.sendIMUData(json.toString())
            imuDataList.removeAll(imuDataList.take(SLIDING_STEP))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    @Composable
    fun WearApp(
        isActive: Boolean,
        prediction: String,
        history: List<String>,
        uiState: com.hogumiwarts.myapplication.presentation.viewmodel.GestureUiState,
        onToggleMeasurement: () -> Unit
    ) {
        MyApplicationTheme {
            val pagerState = rememberPagerState(initialPage = 0)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                // 인식 활성화 인디케이터
                if (uiState.showActivationIndicator) {
                    ActivationIndicator(isActive = isActive)
                }

                HorizontalPager(
                    count = 2,
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> PredictionScreen(
                            isActive = isActive,
                            prediction = prediction,
                            history = history,
                            uiState = uiState,
                            onToggleMeasurement = onToggleMeasurement
                        )
                    }
                }

                // 연결 상태 인디케이터
                ConnectionStatusIndicator(
                    isConnected = uiState.isConnected,
                    isConnecting = uiState.isConnecting,
                    errorMessage = uiState.errorMessage
                )
            }
        }
    }

    @Composable
    fun PredictionScreen(
        isActive: Boolean,
        prediction: String,
        history: List<String>,
        uiState: com.hogumiwarts.myapplication.presentation.viewmodel.GestureUiState,
        onToggleMeasurement: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 제스처 인식 상태와 예측 결과를 결합하여 표시
            val displayText = buildDisplayText(isActive, prediction)

            Text(
                text = displayText,
                color = if (isActive) Color.White else Color.Gray,
                style = MaterialTheme.typography.display1,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 활성화/비활성화 버튼
            Button(
                onClick = onToggleMeasurement,
                modifier = Modifier.fillMaxWidth(0.8f),
                colors = androidx.wear.compose.material.ButtonDefaults.buttonColors(
                    backgroundColor = if (isActive) MaterialTheme.colors.primary else Color.DarkGray
                )
            ) {
                Text(
                    text = if (isActive) "제스처 인식 중..." else "인식 시작(수동)",
                    style = MaterialTheme.typography.title3
                )
            }

            // 짧은 상태 메시지 표시
            AnimatedVisibility(
                visible = isActive,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(
                    text = "제스처 1을 두 번 하면 비활성화",
                    color = Color.Gray,
                    style = MaterialTheme.typography.caption2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }

    // 표시 텍스트를 생성하는 도우미 함수
    private fun buildDisplayText(isActive: Boolean, prediction: String): String {
        // 특수한 경우 처리 (예측 없음, 초기 상태 등)
        if (prediction == "예측 없음" || prediction == "-1" || prediction == "0") {
            return if (isActive) "On : -" else "Off : -"
        }

        // 일반적인 제스처 번호에 대한 처리
        try {
            val gestureId = prediction.toInt()
            return if (isActive) "On : $gestureId" else "Off : $gestureId"
        } catch (e: NumberFormatException) {
            // 숫자로 변환할 수 없는 경우 (예: "idle" 또는 제스처 이름)
            return if (isActive) "On : $prediction" else "Off : $prediction"
        }
    }

    /**
     * 제스처 인식 활성화 상태를 표시하는 애니메이션 인디케이터
     */
    @Composable
    fun ActivationIndicator(isActive: Boolean) {
        val infiniteTransition = rememberInfiniteTransition()
        val pulseAnimation by infiniteTransition.animateFloat(
            initialValue = 0.6f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        val alpha = if (isActive) pulseAnimation else 0f

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            // 화면 상단에 작은 펄스 효과 원
            Canvas(
                modifier = Modifier
                    .size(12.dp)
                    .padding(top = 4.dp)
            ) {
                // 배경 원
                drawCircle(
                    color = Color.Green.copy(alpha = 0.3f * alpha),
                    radius = size.minDimension / 1.5f
                )

                // 내부 원
                drawCircle(
                    color = Color.Green.copy(alpha = 0.8f * alpha),
                    radius = size.minDimension / 3f
                )
            }
        }
    }

    /**
     * 서버 연결 상태 인디케이터
     */
    @Composable
    fun ConnectionStatusIndicator(
        isConnected: Boolean,
        isConnecting: Boolean,
        errorMessage: String?
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val statusColor = when {
                errorMessage != null -> Color.Red
                isConnected -> Color.Green
                isConnecting -> Color.Yellow
                else -> Color.Gray
            }

            // 상태 표시 원
            Box(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )

            // 에러 메시지 표시
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.caption2,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
        }
    }
}