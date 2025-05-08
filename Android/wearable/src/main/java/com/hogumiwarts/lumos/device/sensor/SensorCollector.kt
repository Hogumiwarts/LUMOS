package com.hogumiwarts.lumos.device.sensor


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class SensorCollector(context: Context, private val windowSize: Int = 50) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val buffer: ArrayDeque<FloatArray> = ArrayDeque()

    val currentData: List<FloatArray>
        get() = buffer.toList()

    fun reset(){
        repeat(25) {
            if (buffer.isNotEmpty()) {
                buffer.removeFirst()
            }
        }
    }

    fun start() {
        Log.d("TAG", "start: ")
        accSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        gyroSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
        buffer.clear()
    }

    private var currentAccel: FloatArray? = null
    private var currentGyro: FloatArray? = null

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> currentAccel = it.values.copyOf()
                Sensor.TYPE_GYROSCOPE -> currentGyro = it.values.copyOf()
                else -> return
            }

            // 두 센서가 모두 수집된 경우만 저장
            if (currentAccel != null && currentGyro != null) {
                val combined = FloatArray(6).apply {
                    // 가속도 x,y,z
                    this[0] = currentAccel!![0]
                    this[1] = currentAccel!![1]
                    this[2] = currentAccel!![2]
                    // 자이로 x,y,z
                    this[3] = currentGyro!![0]
                    this[4] = currentGyro!![1]
                    this[5] = currentGyro!![2]
                }

                if (buffer.size >= windowSize) buffer.removeFirst()
                buffer.addLast(combined)

                // 버퍼 상태 로그 출력
//                buffer.forEachIndexed { index, values ->
//                    Log.d("SensorBuffer", "[$index]: ${values.joinToString(", ") { "%.3f".format(it) }}")
//                }

                // 센서 누적 초기화
                currentAccel = null
                currentGyro = null
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}