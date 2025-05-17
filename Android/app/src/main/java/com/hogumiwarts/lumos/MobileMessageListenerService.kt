package com.hogumiwarts.lumos

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class MobileMessageListenerService : WearableListenerService() {

    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/watch_to_mobile_text") {
            val receivedText = String(messageEvent.data)

            // 전역 저장소 또는 ViewModel에 저장
            Log.d("MobileReceiver", "받은 메시지: $receivedText")

            // 예: 브로드캐스트로 UI에 전달
            val intent = Intent("WATCH_MESSAGE")
            intent.putExtra("message", receivedText)
            sendBroadcast(intent)
        }

        if (messageEvent.path == "/open_app") {
            val launchIntent = Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(launchIntent)
        }
    }
}
