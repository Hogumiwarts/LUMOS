package com.hogumiwarts.myapplication.util

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.hogumiwarts.myapplication.R

class SensorService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var accelSensor: Sensor? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        accelSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }

        startForegroundNotification()
    }

    private fun startForegroundNotification() {
        val channelId = "sensor_channel"
        val channelName = "IMU Foreground Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("제스처 인식 중")
            .setContentText("IMU 센서 데이터를 수집하고 있어요")
            .setSmallIcon(R.drawable.ic_star) // 반드시 존재해야 함
            .setOngoing(true) // 사용자가 끌 수 없게 고정
            .build()

        startForeground(1, notification)
        Log.d("TAG", "startForegroundNotification: Foreground 시작")
    }


    override fun onSensorChanged(event: SensorEvent?) {
        // TODO: 센서 데이터 처리
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        sensorManager.unregisterListener(this)


        Log.d("SensorService", "🛑 서비스 종료됨, 센서 해제")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}