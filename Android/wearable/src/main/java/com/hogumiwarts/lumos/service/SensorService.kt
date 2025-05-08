package com.hogumiwarts.lumos.service
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
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
                if (data.size >= 50) {
                    val normalized = SensorNormalizer.normalize(data)
                    val result = predictGesture(interpreter, normalized, labelMap)
                    if (result.first == "motion1" && result.second > 0.8f) {
                        NotificationUtils.showGestureNotification(this@SensorService, "모션 감지됨!", "motion1")
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
    data: Array<FloatArray>,
    labelMap: Map<Int, String>
): Pair<String, Float> {
    val output = Array(1) { FloatArray(labelMap.size) }
    interpreter.run(data, output)

    val predictedIndex = output[0].indices.maxByOrNull { output[0][it] } ?: -1
    val confidence = output[0][predictedIndex]
    val label = labelMap[predictedIndex] ?: "Unknown"

    return label to confidence
}