package com.hogumiwarts.lumos.device.ml

import android.content.Context
import org.json.JSONObject

object LabelMapLoader {

    fun loadLabelMap(context: Context): Map<Int, String> {
        val inputStream = context.assets.open("label_map.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(jsonString)
        val labels = jsonObject.getJSONObject("labels")

        return labels.keys().asSequence().associate { key ->
            key.toInt() to labels.getString(key)
        }
    }
}