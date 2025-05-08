package com.example.myapplication.presentation.ui

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import com.example.myapplication.data.model.GestureData
import com.example.myapplication.data.model.ImuRequest
import com.example.myapplication.presentation.viewmodel.TestViewModel
import com.example.myapplication.theme.MyApplicationTheme
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.AndroidEntryPoint
import android.provider.Settings
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


// MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity(), SensorEventListener {
    private lateinit var sensorManager: SensorManager // 개객 선언 SensorManager
    private var accelerometer: Sensor? = null // Accelerometer Sensor
    private var linearAccelerometer: Sensor? = null // linearAccelerometer Sensor
    private var gyroscope: Sensor? = null // Gyroscope Sensor

    private var isMeasuring by mutableStateOf(false) // 측정 상태 저장 (Compose UI용)
    private val imuDataList = mutableListOf<GestureData>() // IMU 데이터 저장 리스트

    private var tempLINEARAccel: FloatArray?=null// 임시 저장용 가속도 데이터
    private var tempAccel: FloatArray?=null// 임시 저장용 가속도 데이터
    private var tempGyro: FloatArray?=null// 임시 저장용 자이로 데이터
    private var lastTimestamp: Long = 0 // 마지막 데이터 수집 시간

    private val testViewModel: TestViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen() // Splash 화면 설치
        super.onCreate(savedInstanceState)


        setTheme(android.R.style.Theme_DeviceDefault) // 기본 테마 설정


        // Sensor Manager 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        // Compose UI 세팅
        setContent {
            AppNavigation(this)
//            WearApp(
//                isMeasuring = isMeasuring,
//                onToggleMeasurement = { toggleIMU() }
//            )
        }
    }
    @Composable
    fun AppNavigation(activity: MainActivity) {
        val navController = rememberNavController()

        NavHost(navController, startDestination = "gesture_screen") {
            composable("gesture_screen") {
                GestureScreen(navController)
            }
            composable("gesture_test/{index}") { backStackEntry ->
                val index = backStackEntry.arguments?.getString("index") ?: "1"
                GestureTest(navController, activity,index)

            }
        }
    }

    // IMU 측정 시작
    private fun startIMU() {
        accelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        linearAccelerometer?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }
        gyroscope?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL) }

        imuDataList.clear() // 데이터 초기화
        isMeasuring = true
    }

    // IMU 측정 중지
    private fun stopIMU() {
        sensorManager.unregisterListener(this)
        isMeasuring = false
//        saveAndSendIMUData() // 데이터 저장 + 전송
    }

    // 버튼 클릭 시 토글
    fun toggleIMU() {
        if (isMeasuring) stopIMU() else startIMU()
        Log.d("IMU", "켜기")
    }

    // 센서 데이터 수신 콜백
    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        when (event.sensor.type) {
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                tempLINEARAccel = event.values.clone()
                lastTimestamp = System.currentTimeMillis()
                trySaveIMUData()
            }
            Sensor.TYPE_ACCELEROMETER-> {
                tempAccel = event.values.clone()
                lastTimestamp = System.currentTimeMillis()
                trySaveIMUData()
            }
            Sensor.TYPE_GYROSCOPE -> {
                tempGyro = event.values.clone()
                lastTimestamp = System.currentTimeMillis()
                trySaveIMUData()
                Log.d("IMU", "${tempGyro?.contentToString()}}")
            }
        }
    }

    // 임시 데이터가 모두 모였으면 저장
    private fun trySaveIMUData() {
            if(tempAccel!=null && tempGyro!=null && tempLINEARAccel!=null){
                val (ax, ay, az) = tempAccel!!
                val (gx, gy, gz) = tempGyro!!
                val (lax, lay, laz) = tempLINEARAccel!!

                imuDataList.add(
                    GestureData(
                        timestamp = lastTimestamp.toLong(),
                        liAccX = lax,
                        liAccY = lay,
                        liAccZ = laz,
                        accX = ax,
                        accY = ay,
                        accZ = az,
                        gryoX = gx,
                        gryoY = gy,
                        gryoZ = gz
                    ))
            }


            // 다음 데이터 수집을 위해 초기화


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // IMU 데이터 저장 및 스마트폰으로 전송
    @SuppressLint("HardwareIds")
    fun saveAndSendIMUData(name:String) {

        val androidId = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        lifecycleScope.launch{
            val a = testViewModel.postTest(ImuRequest(
                gestureId = name.toLong(),
                watchDeviceId = androidId,
                data= imuDataList
            ))

            a.onSuccess {

                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this@MainActivity, "전송이 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }


        // 모바일을 통해서 메일 전송
//        val nodeClient = Wearable.getNodeClient(this)
//
//        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
//            for (node in nodes) {
//                val csvHeader = "timestamp,li_acc_x,li_acc_y,li_acc_z,acc_x,acc_y,acc_z,gryo_x,gryo_y,gryo_z\n"
//                val csvBody = imuDataList.joinToString("\n")
//                val csvData = (csvHeader + csvBody).toByteArray()
//
//                Wearable.getMessageClient(this).sendMessage(
//                    node.id,
//                    "imu_data$name",
//                    csvData
//                ).addOnSuccessListener {
//                    Log.d("IMU", "\ud83d\udc49 데이터 전송 성공 to ${node.displayName}")
//                }.addOnFailureListener { e ->
//                    Log.e("IMU", "\ud83d\udc49 데이터 전송 실패 to ${node.displayName}", e)
//                }
//            }
//        }.addOnFailureListener { e ->
//            Log.e("IMU", "\ud83d\udc49 연결된 노드 가져오기 실패", e)
//        }
    }
}

// --- Compose UI 부분 ---

@Composable
fun WearApp(
    isMeasuring: Boolean,
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
            TimeText() // 상단 시간 표시

            HorizontalPager(
                count = 2,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> GreetingWithButton(isMeasuring, onToggleMeasurement) // 첫 페이지 (측정 버튼)
                    1 -> SecondScreen() // 두 번째 페이지
                }
            }
        }
    }
}

@Composable
fun GreetingWithButton(
    isMeasuring: Boolean,
    onToggleMeasurement: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.wear.compose.material.Button(
            modifier = Modifier.fillMaxWidth(0.8f),
            onClick = { onToggleMeasurement() }
        ) {
            Text(
                text = if (isMeasuring) "측정 중지" else "IMU 측정 시작",
                style = MaterialTheme.typography.title3
            )
        }
    }
}

@Composable
fun SecondScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "두 번째 화면!",
            style = MaterialTheme.typography.title3
        )
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    WearApp(
        isMeasuring = false,
        onToggleMeasurement = {}
    )
}

