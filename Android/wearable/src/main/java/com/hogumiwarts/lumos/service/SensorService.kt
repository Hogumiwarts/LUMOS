package com.hogumiwarts.lumos.service
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.hogumiwarts.lumos.device.ml.LabelMapLoader
import com.hogumiwarts.lumos.device.ml.TFLiteInterpreterProvider
import com.hogumiwarts.lumos.device.sensor.SensorCollector
import com.hogumiwarts.lumos.device.sensor.SensorNormalizer
import org.tensorflow.lite.Interpreter

class SensorService : Service() {

    private lateinit var sensorCollector: SensorCollector
    private lateinit var interpreter: Interpreter
    private lateinit var labelMap: Map<Int, String>
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

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

    private fun startInferenceLoop() {
        handler.post(object : Runnable {
            override fun run() {
                val data = sensorCollector.currentData
                Log.d("TAG", "run: ${data.size}")
                if (data.size >= 50) {
                    val normalized = SensorNormalizer.normalize(data)
                    val result = predictGesture(interpreter, normalized, labelMap)

                    Log.d("TAG", "결과: ${result.first}, 신뢰도: ${result.second}")
                    // 결과 후 앞에서 25개 삭제
                    sensorCollector.reset()

                    if ( result.second > 0.8f) {
                        if(result.first == "motion1"){
                            NotificationUtils.showGestureNotification(this@SensorService, "모션 감지됨!", "motion1")
                        }
                        sensorCollector.clear()
                    }



                }
                handler.postDelayed(this, 500)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorCollector.stop()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

fun predictGesture(
    interpreter: Interpreter,
    data: Array<FloatArray>, // shape: (50, 6)
    labelMap: Map<Int, String>
): Pair<String, Float> {
    // input shape: (1, 50, 6)
    if (data.size != 50 || data.any { it.size != 6 }) {
        Log.e("Predict", "입력 shape 잘못됨: ${data.size}x${data.getOrNull(0)?.size}")
        return "InvalidInput" to 0f
    }

    val input = Array(1) { Array(50) { FloatArray(6) } }
    for (i in 0 until 50) {
        for (j in 0 until 6) {
            input[0][i][j] = data[i][j]
        }
    }

    val output = Array(1) { FloatArray(labelMap.size) }

    return try {
        interpreter.run(input, output)

        val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][predictedIndex]
        val label = labelMap[predictedIndex] ?: "Unknown"

        label to confidence
    } catch (e: Exception) {
        Log.e("Predict", "TFLite 추론 중 오류 발생", e)
        "Crash" to 0f
    }
}
