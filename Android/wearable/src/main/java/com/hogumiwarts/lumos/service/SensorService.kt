package com.hogumiwarts.lumos.service
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import com.hogumiwarts.lumos.device.ml.LabelMapLoader
import com.hogumiwarts.lumos.device.ml.TFLiteInterpreterProvider
import com.hogumiwarts.lumos.device.sensor.SensorCollector
import com.hogumiwarts.lumos.device.sensor.SensorNormalizer
import org.tensorflow.lite.Interpreter
import java.util.Arrays

class SensorService : Service() {

    private lateinit var sensorCollector: SensorCollector
    private lateinit var interpreter: Interpreter
    private lateinit var labelMap: Map<Int, String>
    private val handler = Handler(Looper.getMainLooper())


    // 워치 화면이 꺼져도 계속 감지 하게 설정
    private lateinit var wakeLock: PowerManager.WakeLock

    private var isTestMode = false // ← 여기 선언

    override fun onCreate() {
        super.onCreate()

        // 워치 화면이 꺼져도 계속 감지 하게 설정
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "SensorService::Wakelock"
        )
        wakeLock.acquire()

        Log.d("SensorService", "onCreate 시작됨")

        try {
            NotificationUtils.createNotificationChannel(this)
            startForeground(1, NotificationUtils.foregroundNotification(this))
            Log.d("SensorService", "startForeground 호출됨")
        } catch (e: Exception) {
            Log.e("SensorService", "알림 생성 실패", e)
        }

        NotificationUtils.createNotificationChannel(this)
        startForeground(1, NotificationUtils.foregroundNotification(this))

        sensorCollector = SensorCollector(this)
        sensorCollector.start()

        interpreter = TFLiteInterpreterProvider.getInterpreter(this)
        labelMap = LabelMapLoader.loadLabelMap(this)

        startInferenceLoop()
    }

    // ✅ 여기 추가
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        isTestMode = intent?.getBooleanExtra("isTest", false) ?: false
        Log.d("SensorService", "onStartCommand 호출됨, isTestMode = $isTestMode")
        return START_STICKY
    }

    private fun startInferenceLoop() {
        handler.post(object : Runnable {
            override fun run() {
                val data = sensorCollector.currentData
                Log.d("TAG", "run: ${data.size}")
                data.forEach{
                        Log.d("TAG", "run: ${Arrays.toString(it)}")
                }

                if (data.size >= 50) {


//                    val normalized = SensorNormalizer.normalize(data)
//                    val result = predictGesture(interpreter, normalized, labelMap)

//                    Log.d("TAG", "결과: ${result.first}, 신뢰도: ${result.second}")

                    // 결과 후 앞에서 25개 삭제
//                    sensorCollector.reset()

//                    if ( result.second > 0.8f) {
//                        if(result.first == "motion1"){
//                            NotificationUtils.showGestureNotification(this@SensorService, "모션 감지됨!", "motion1")
//                            Log.d("TAG", "isTestMode: $isTestMode")
//                        }
//
//                        if(isTestMode){ // 테스트용: 결과 브로드캐스트
//                            val intent = Intent("GESTURE_RESULT")
//                            intent.putExtra("gesture", result.first) // 예: "motion1"
//                                .setPackage(packageName)
//                            Log.d("TAG", "브로드캐스트 보내기: $result")
//                            applicationContext.sendBroadcast(intent)
//                        }
//
//
//                        sensorCollector.clear()
//                    }



                }
                handler.postDelayed(this, 500)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(true)
        handler.removeCallbacksAndMessages(null)
        sensorCollector.stop()

        if (::wakeLock.isInitialized && wakeLock.isHeld) {
            wakeLock.release()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
