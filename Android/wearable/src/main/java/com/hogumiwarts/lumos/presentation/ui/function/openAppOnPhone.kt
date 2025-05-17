package com.hogumiwarts.lumos.presentation.ui.function

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.gms.wearable.Wearable


fun sendOpenAppMessage(context: Context) {
    val messageClient = Wearable.getMessageClient(context)
    val path = "/open_app"

    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            for (node in nodes) {
                messageClient.sendMessage(node.id, path, "".toByteArray())
            }
        }
}