package com.hogumiwarts.lumos.presentation.ui.function

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.wearable.Wearable
import com.google.gson.JsonObject


fun sendOpenAppMessage(context: Context) {
    val messageClient = Wearable.getMessageClient(context)
    val path = "/open_app"
    Log.d("Mobile", "sendOpenAppMessage: ")
    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            for (node in nodes) {
                messageClient.sendMessage(node.id, path, "".toByteArray())
            }
        }
}

fun sendOpenLightMessage(context: Context, deviceId: Long, deviceType: String) {
    val messageClient = Wearable.getMessageClient(context)
    val path = "/open_device"
    val payload = JsonObject().apply {
        addProperty("deviceId", deviceId)
        addProperty("deviceType", deviceType)
    }.toString().toByteArray()

    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            for (node in nodes) {
                messageClient.sendMessage(node.id, path, payload)
            }
        }
}