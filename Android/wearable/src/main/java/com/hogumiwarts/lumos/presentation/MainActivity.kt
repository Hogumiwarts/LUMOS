package com.hogumiwarts.lumos.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.wearable.Wearable
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.data.GestureData
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.navigation.NavGraph
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureMode
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureTestScreen
import com.hogumiwarts.lumos.presentation.ui.screens.routine.RoutineExecuteScreen
import com.hogumiwarts.lumos.presentation.ui.viewmodel.WebSocketViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var linearAccelerometer: Sensor? = null
    private var gyroscope: Sensor? = null

    private var isMeasuring by mutableStateOf(false)
    private val imuDataList = mutableListOf<GestureData>()

    private var tempLINEARAccel: FloatArray? = null
    private var tempAccel: FloatArray? = null
    private var tempGyro: FloatArray? = null
    private var lastTimestamp: Long = 0

    private val webSocketViewModel: WebSocketViewModel by viewModels()

    private val SLIDING_WINDOW_SIZE = 50
    private val SLIDING_STEP = 10
    private val SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME

    // 현재 제스처 모드
    private var gestureMode = GestureMode.CONTINUOUS


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val text = intent.getStringExtra("text") ?: ""
        gestureMode = if (text.isNotEmpty()) {
            GestureMode.TEST
        } else {
            GestureMode.CONTINUOUS
        }
        Log.d("Gesture", "결정된 모드: $gestureMode (text 존재: ${text.isNotEmpty()})")

        lifecycleScope.launch {
            webSocketViewModel.test1.collect { value ->
                // 필요한 처리
                if (value){
                    imuDataList.clear()
                    webSocketViewModel.resetTest1()
                }
            }
        }

        lifecycleScope.launch {
            webSocketViewModel.isTest.collect { value ->
                // 필요한 처리
                if (value){
                    Log.d("테스트 실행 여부", "onCreate: 실행")
                }else{
                    Log.d("테스트 실행 여부", "onCreate: 정지")
                }
            }
        }

        lifecycleScope.launch {
            webSocketViewModel.gesture2.collect { value ->
                // 필요한 처리
                if (value){
                    Log.d("제스처2", "onCreate: 인식")
                }else{
                    Log.d("제스처2", "onCreate: 해제")
                }
            }
        }

//        if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1001)
//        }

        // 진동 권한 확인 (API 33 이상에서 필요)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
//            if (checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(arrayOf(Manifest.permission.VIBRATE), 1002)
//            }
//        }

        // 센서 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // 진동 초기화
        webSocketViewModel.initVibrator(this)

        // 제스처 콜백 설정 (중요!)
        webSocketViewModel.onGesture1Detected = {
            Log.d("Gesture", "제스처 1 감지됨")
            // 1번 제스처는 단순히 모드 토글용이므로 버퍼만 지우기
            imuDataList.clear()
        }

        webSocketViewModel.onGesture2Detected = {
            Log.d("Gesture", "ACTIVE 상태: 제스처 2 감지됨")
            // 2,3번 제스처 감지 후 잠시 데이터 수집 중단 (연속 감지 방지)
            clearIMUDataAndPauseCollection()
        }

        webSocketViewModel.onGesture3Detected = {
            Log.d("Gesture", "ACTIVE 상태: 제스처 3 감지됨")
            // 2,3번 제스처 감지 후 잠시 데이터 수집 중단 (연속 감지 방지)
            clearIMUDataAndPauseCollection()
        }

        // CONTINUOUS 모드에서만 자동으로 시작
        if (gestureMode == GestureMode.CONTINUOUS) {
            isMeasuring = true
//            startIMU()
            webSocketViewModel.connectWebSocket(gestureMode)
            Log.d("Gesture", "CONTINUOUS 모드 - 웹소켓 연결 및 센서 수집 시작")
        } else {
            Log.d("Gesture", "TEST 모드 - 수동 시작 대기 중")
        }


        setContent {
            LUMOSTheme {

                val prediction by webSocketViewModel.prediction
                val currentExecutingRoutine by webSocketViewModel.currentExecutingRoutine.collectAsState()

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

                    when (gestureMode) {
                        // 테스트 모드
                        GestureMode.TEST -> {
                            GestureTestScreen(
                                type = text,
                                isMeasuring = isMeasuring,
                                prediction = prediction,
                                onToggleMeasurement = {
                                    isMeasuring = !isMeasuring
                                    if (isMeasuring) {
                                        startIMU()
                                        webSocketViewModel.connectWebSocket(gestureMode)
                                    } else {
                                        stopIMU()
                                        webSocketViewModel.disconnectWebSocket()
                                    }
                                }
                            ) {
                                sendTextToMobile(this@MainActivity, it)
                                Log.d("Gesture", "제스처 테스트 결과: $it")
                                finish()
                            }
                        }
                        // 연속 감지 모드
                        GestureMode.CONTINUOUS -> {
                            val navController = rememberNavController()
                            NavGraph(navController)

                            // 루틴 실행 화면 띄우기
                            currentExecutingRoutine?.let { routineId ->
                                Log.d("Execution", "루틴 화면 표시: 제스처 $routineId")
                                RoutineExecuteScreen(routineId)
                            }

//                            if (recognitionMode == WebSocketViewModel.GestureRecognitionMode.ACTIVE) {
//                                prediction.toLongOrNull()?.let { longValue ->
//                                    if (longValue in listOf(2L, 3L)) {
//                                        Log.d("Routine", "ACTIVE 모드에서 루틴 화면 표시: 제스처 $longValue")
//                                        RoutineExecuteScreen(longValue)
//                                    }
//                                }
//                            }
                        }
                    }
                }
            }
        }
    }

    private fun startIMU() {
        sensorManager.registerListener(this, accelerometer, SENSOR_DELAY)
        sensorManager.registerListener(this, linearAccelerometer, SENSOR_DELAY)
        sensorManager.registerListener(this, gyroscope, SENSOR_DELAY)
        imuDataList.clear()
        isMeasuring = true
    }

    private fun stopIMU() {
        sensorManager.unregisterListener(this)
        isMeasuring = false
        imuDataList.clear()
    }

    // 데이터 수집 일시 정지 함수 추가
    private fun clearIMUDataAndPauseCollection() {
        // 버퍼 지우기
        imuDataList.clear()

        // 잠시 측정 중단 플래그 설정
        isMeasuring = false

//        // 1.5초 후 측정 재개
//        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
//            isMeasuring = true
//            Log.d("IMU", "데이터 수집 재개")
//        }, 1500)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isMeasuring) return  // 측정 중일 때만 처리

        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> tempLINEARAccel = event.values.clone()
            Sensor.TYPE_ACCELEROMETER -> tempAccel = event.values.clone()
            Sensor.TYPE_GYROSCOPE -> tempGyro = event.values.clone()
        }
        lastTimestamp = System.currentTimeMillis()
        trySaveIMUData()
        trySendSlidingWindow()
    }

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

            Log.d("IMU", "✅ 저장됨: ${imuDataList.size}개")

            // 초기화! 중복 저장 방지
            tempAccel = null
            tempGyro = null
            tempLINEARAccel = null
        }
    }

    private fun trySendSlidingWindow() {
        if (imuDataList.size >= SLIDING_WINDOW_SIZE) {
            val window = imuDataList.takeLast(SLIDING_WINDOW_SIZE)

            // JSON 배열 크기 로그로 출력
            Log.d("WebSocket", "전송할 윈도우 크기: ${window.size}")

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

            webSocketViewModel.sendIMUData(json.toString())
            imuDataList.removeAll(imuDataList.take(SLIDING_STEP))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        stopIMU()
        webSocketViewModel.disconnectWebSocket()
        Log.d("Gesture", "MainActivity 종료 - 리소스 정리 완료")
    }
}

fun sendTextToMobile(context: Context, message: String) {
    val messageClient = Wearable.getMessageClient(context)
    val path = "/watch_to_mobile_text"

    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("TAG", "sendTextToMobile: 보내기")
                messageClient.sendMessage(node.id, path, message.toByteArray())
            }
        }
}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
//        DevicesScreen()
    }
}