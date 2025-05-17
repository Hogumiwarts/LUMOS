package com.hogumiwarts.lumos.service

import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.google.gson.JsonParser
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

        if (messageEvent.path == "/launch_send_token") {
            val receivedText = String(messageEvent.data)
            Log.d("TAG", "onMessageReceived: $receivedText")
            val jsonElement = JsonParser.parseString(receivedText)
            val token = jsonElement.asJsonObject.get("token").asString
            val intent = Intent("TOKEN_RECEIVED").apply {
                putExtra("token", token)
            }
            sendBroadcast(intent)
        }
    }
}
