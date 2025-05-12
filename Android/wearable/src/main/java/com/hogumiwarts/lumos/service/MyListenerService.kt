package com.hogumiwarts.lumos.service

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.hogumiwarts.lumos.presentation.MainActivity

class MyListenerService : WearableListenerService() {
    override fun onMessageReceived(messageEvent: MessageEvent) {
        if (messageEvent.path == "/launch_text_display") {
            val receivedText = String(messageEvent.data)

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra("text", receivedText)
            }
            startActivity(intent)
        }
    }
}
