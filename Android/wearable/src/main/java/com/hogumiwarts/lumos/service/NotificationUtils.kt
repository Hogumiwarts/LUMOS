package com.hogumiwarts.lumos.service


import android.app.*
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hogumiwarts.lumos.R

object NotificationUtils {
    private const val CHANNEL_ID = "motion_channel"
    private const val CHANNEL_NAME = "Motion Detection"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun foregroundNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("제스처 인식 중")
            .setContentText("워치에서 센서를 감지하고 있습니다.")
            .setSmallIcon(R.drawable.ic_light)
            .build()
    }

    fun showGestureNotification(context: Context, title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_light)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1001, notification)
    }
}