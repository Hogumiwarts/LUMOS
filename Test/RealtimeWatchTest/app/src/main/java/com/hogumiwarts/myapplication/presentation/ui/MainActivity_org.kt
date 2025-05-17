//package com.hogumiwarts.myapplication.presentation.ui
//
//import android.content.Context
//import android.hardware.Sensor
//import android.hardware.SensorEvent
//import android.hardware.SensorEventListener
//import android.hardware.SensorManager
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.util.Log
//import android.view.WindowManager
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.viewModels
//import androidx.compose.foundation.background
//import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
//import androidx.compose.foundation.layout.*
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.wear.compose.material.MaterialTheme
//import androidx.wear.compose.material.Text
//import com.hogumiwarts.myapplication.data.model.GestureData
//import dagger.hilt.android.AndroidEntryPoint
//import com.hogumiwarts.myapplication.presentation.viewmodel.GestureViewModel
//import com.hogumiwarts.myapplication.theme.MyApplicationTheme
//import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.rememberPagerState
//import org.json.JSONArray
//import org.json.JSONObject
//
//
//@AndroidEntryPoint
//class MainActivity : ComponentActivity(), SensorEventListener {
//    private lateinit var sensorManager: SensorManager
//    private var accelerometer: Sensor? = null
//    private var linearAccelerometer: Sensor? = null
//    private var gyroscope: Sensor? = null
//
//    private var isMeasuring by mutableStateOf(false)
//    private val imuDataList = mutableListOf<GestureData>()
//
//    private var tempLINEARAccel: FloatArray? = null
//    private var tempAccel: FloatArray? = null
//    private var tempGyro: FloatArray? = null
//    private var lastTimestamp: Long = 0
//
//    private val gestureViewModel: GestureViewModel by viewModels()
//
//    private val SLIDING_WINDOW_SIZE = 50
//    private val SLIDING_STEP = 5
//    private val SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
//        super.onCreate(savedInstanceState)
//        setTheme(android.R.style.Theme_DeviceDefault)
//
//        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
//
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
//        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
//        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
//
//        setContent {
//            val prediction by gestureViewModel.prediction
//            val history = gestureViewModel.history
//
//            WearApp(
//                isMeasuring = isMeasuring,
//                prediction = prediction,
//                history = history,
//                onToggleMeasurement = {
//                    isMeasuring = !isMeasuring
//                    if (isMeasuring) {
//                        startIMU()
//                        gestureViewModel.connectWebSocket()
//                    } else {
//                        stopIMU()
//                        gestureViewModel.disconnectWebSocket()
//                    }
//                }
//            )
//        }
//
//
//        // Callback
//        gestureViewModel.onGesture1Detected = {
//            // 제스처 1이 감지되었을 때 실행할 코드
//            Log.d("Gesture", "제스처 1 감지됨!")
//            // 여기에 원하는 동작 구현 (예: 알람 울리기, 화면 전환 등)
//            //playBeepSound()  // 예시 함수
//            clearIMUDataAndPauseCollection()
//
//        }
//
//        gestureViewModel.onGesture2Detected = {
//            Log.d("Gesture", "제스처 2 감지됨!")
//           // vibrate()  // 예시 함수
//            clearIMUDataAndPauseCollection()
//
//        }
//
//        gestureViewModel.onGesture3Detected = {
//            Log.d("Gesture", "제스처 3 감지됨!")
//           // navigateToScreen()  // 예시 함수
//            clearIMUDataAndPauseCollection()
//
//        }
//
////        gestureViewModel.onGesture4Detected = {
////            Log.d("Gesture", "제스처 4 감지됨!")
////           // toggleFlashlight()  // 예시 함수
////            clearIMUDataAndPauseCollection()
////
////        }
//    }// ...onCreate
//
//    // 0517
//    private fun clearIMUDataAndPauseCollection() {
//        // 1. 버퍼 지우기
//        imuDataList.clear()
//
//        // 2. 옵션: 짧은 시간 동안 측정 일시 중지 (연속 감지 방지)
//        isMeasuring = false
//
//        // 3. 일정 시간 후 다시 측정 시작 (예: 1초 후)
//        Handler(Looper.getMainLooper()).postDelayed({
//            if (!isMeasuring) {  // 사용자가 수동으로 중지하지 않았다면
//                isMeasuring = true
//            }
//        }, 1000)  // 1000ms = 1초
//    }
//
//    private fun startIMU() {
//        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY)
//        sensorManager.registerListener(this, linearAccelerometer, SENSOR_DELAY)
//        sensorManager.registerListener(this, gyroscope, SENSOR_DELAY)
//        imuDataList.clear()
//        isMeasuring = true
//    }
//
//    private fun stopIMU() {
//        sensorManager.unregisterListener(this)
//        isMeasuring = false
//        imuDataList.clear()
//    }
//
//    override fun onSensorChanged(event: SensorEvent?) {
//        if (!isMeasuring) return  // 측정 중일 때만 처리
//
//        event ?: return
//
//        when (event.sensor.type) {
//            Sensor.TYPE_LINEAR_ACCELERATION -> tempLINEARAccel = event.values.clone()
//            Sensor.TYPE_ACCELEROMETER -> tempAccel = event.values.clone()
//            Sensor.TYPE_GYROSCOPE -> tempGyro = event.values.clone()
//        }
//        lastTimestamp = System.currentTimeMillis()
//        trySaveIMUData()
//        trySendSlidingWindow()
//    }
//
//    private fun trySaveIMUData() {
//        if (tempAccel != null && tempGyro != null && tempLINEARAccel != null) {
//            val (ax, ay, az) = tempAccel!!
//            val (gx, gy, gz) = tempGyro!!
//            val (lax, lay, laz) = tempLINEARAccel!!
//
//            imuDataList.add(
//                GestureData(
//                    timestamp = lastTimestamp,
//                    accX = ax, accY = ay, accZ = az,
//                    liAccX = lax, liAccY = lay, liAccZ = laz,
//                    gryoX = gx, gryoY = gy, gryoZ = gz
//                )
//            )
//
//            // Log.d("IMU", "✅ 저장됨: ${imuDataList.size}개")
//
//            // 초기화! 중복 저장 방지
//            tempAccel = null
//            tempGyro = null
//            tempLINEARAccel = null
//        }
//    }
//
//    private fun trySendSlidingWindow() {
//        if (imuDataList.size >= SLIDING_WINDOW_SIZE) {
//            val window = imuDataList.takeLast(SLIDING_WINDOW_SIZE)
//
//            // JSON 배열 크기 로그로 출력
//            // Log.d("WebSocket", "전송할 윈도우 크기: ${window.size}")
//
//            if (window.size != SLIDING_WINDOW_SIZE) {
//                Log.e("WebSocket", "❌ 슬라이딩 윈도우 크기가 맞지 않습니다!")
//                return
//            }
//
//            val json = JSONObject().apply {
//                put("gesture_id", 0)
//                put("data", JSONArray().apply {
//                    window.forEach {
//                        put(JSONObject().apply {
//                            put("timestamp", it.timestamp)
//                            put("acc_x", it.accX)
//                            put("acc_y", it.accY)
//                            put("acc_z", it.accZ)
//                            put("li_acc_x", it.liAccX)
//                            put("li_acc_y", it.liAccY)
//                            put("li_acc_z", it.liAccZ)
//                            put("gryo_x", it.gryoX)
//                            put("gryo_y", it.gryoY)
//                            put("gryo_z", it.gryoZ)
//                        })
//                    }
//                })
//            }
//
//            gestureViewModel.sendIMUData(json.toString())
//            imuDataList.removeAll(imuDataList.take(SLIDING_STEP))
//        }
//    }
//
//    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
//
//    @Composable
//    fun WearApp(
//        isMeasuring: Boolean,
//        prediction: String,
//        history: List<String>,
//        onToggleMeasurement: () -> Unit
//    ) {
//        MyApplicationTheme {
//            val pagerState = rememberPagerState(initialPage = 0)
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(MaterialTheme.colors.background),
//                contentAlignment = Alignment.Center
//            ) {
//                HorizontalPager(
//                    count = 2,
//                    state = pagerState,
//                    modifier = Modifier.fillMaxSize()
//                ) { page ->
//                    when (page) {
//                        0 -> PredictionScreen(
//                            isMeasuring = isMeasuring,
//                            prediction = prediction,
//                            history = history,
//                            onToggleMeasurement = onToggleMeasurement
//                        )
//                    }
//                }
//            }
//        }
//    }
//
//    @Composable
//    fun PredictionScreen(
//        isMeasuring: Boolean,
//        prediction: String,
//        history: List<String>,
//        onToggleMeasurement: () -> Unit
//    ) {
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
////            Text("🔍 예측 결과", color = Color.White)
//            Spacer(modifier = Modifier.height(6.dp))
//            Text(prediction, color = Color.White, style = MaterialTheme.typography.display1)
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            androidx.wear.compose.material.Button(
//                onClick = onToggleMeasurement,
//                modifier = Modifier.fillMaxWidth(0.8f)
//            ) {
//                Text(
//                    text = if (isMeasuring) "측정 중지" else "측정 시작",
//                    style = MaterialTheme.typography.title3
//                )
//            }
//
////            Spacer(modifier = Modifier.height(8.dp))
////
////            Text("📜 히스토리", color = Color.Gray)
////            history.take(5).forEach { item ->
////                Text(text = item, color = Color.White, style = MaterialTheme.typography.caption2)
////            }
//        }
//    }
//}
//
//
