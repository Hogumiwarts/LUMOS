package com.hogumiwarts.lumos.device.sensor


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class SensorCollector(context: Context, private val windowSize: Int = 50) : SensorEventListener {

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val accSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val buffer: ArrayDeque<FloatArray> = ArrayDeque()

    val currentData: List<FloatArray>
        get() = buffer.toList()

    fun start() {
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

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val data = when (it.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> it.values.copyOf() // [x, y, z]
                Sensor.TYPE_GYROSCOPE -> it.values.copyOf() // [x, y, z]
                else -> return
            }

            if (buffer.size >= windowSize) buffer.removeFirst()
            buffer.addLast(data)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}