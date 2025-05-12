package com.hogumiwarts.lumos.ui.screens.gesture.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.wearable.Wearable
import com.hogumiwarts.lumos.GestureTestViewModel

@Composable
fun MessageReceiver(viewModel: GestureTestViewModel) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val msg = intent.getStringExtra("message") ?: return
                Log.d("TAG", "onReceive: $msg")
                viewModel.updateMessage(msg)
            }
        }

        val filter = IntentFilter("WATCH_MESSAGE")
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}


fun sendTextToWatch(context: Context, message: String) {
    val messageClient = Wearable.getMessageClient(context)
    val path = "/launch_text_display"


    // 워치 노드 가져오기
    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            for (node in nodes) {
                messageClient.sendMessage(node.id, path, message.toByteArray())
                    .addOnSuccessListener {
                        Log.d("Mobile", "메시지 전송 성공")
                    }
                    .addOnFailureListener {
                        Log.e("Mobile", "메시지 전송 실패: ${it.message}")
                    }
            }
        }
}