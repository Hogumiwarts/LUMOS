package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.FileProvider
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity(), MessageClient.OnMessageReceivedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // ✨ Wearable 메시지 수신 리스너 등록
        Wearable.getMessageClient(this).addListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()

        // ✨ 앱 종료 시 리스너 해제
        Wearable.getMessageClient(this).removeListener(this)
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "imu_data1" || messageEvent.path == "imu_data2" || messageEvent.path == "imu_data3" || messageEvent.path == "imu_data4") {
            Log.d("IMU", "👉 데이터 수신됨!")

            val fileBytes = messageEvent.data

            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

            val currentTime = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("MMddHHmm", Locale.KOREA)
            val formattedTime = dateFormat.format(Date(currentTime))
            // ✨ 받은 데이터로 파일 저장
            val file = File(cacheDir, "${messageEvent.path}_${deviceId}_$formattedTime.csv")
            file.writeBytes(fileBytes)

            // ✨ 파일 URI 가져오기
            val uri: Uri = FileProvider.getUriForFile(
                this,
                "$packageName.fileprovider",
                file
            )

            // ✨ 이메일 Intent 만들기
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("gaga9353@naver.com"))
                putExtra(Intent.EXTRA_SUBJECT, "IMU 데이터 전송 (스마트폰)")
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(emailIntent, "이메일 보내기"))
        }
    }
}
