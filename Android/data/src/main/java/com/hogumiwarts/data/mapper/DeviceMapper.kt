package com.hogumiwarts.data.mapper

import com.hogumiwarts.domain.model.DeviceResult
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun DeviceResult.toDomain(): DeviceResult {
    return DeviceResult(
        tagNumber = tagNumber,
        deviceId = deviceId,
        installedAppId = installedAppId,
        deviceImg = deviceImg,
        deviceName = deviceName,
        deviceType = deviceType,
        activated = activated
    )
}

fun parseCommands(commands: JsonElement): List<CommandData> {
    return try {
        val commandArray = commands.jsonArray
        commandArray.map { element ->
            val obj = element.jsonObject
            CommandData(
                component = obj["component"]?.jsonPrimitive?.content ?: "",
                capability = obj["capability"]?.jsonPrimitive?.content ?: "",
                command = obj["command"]?.jsonPrimitive?.content ?: "",
                arguments = obj["arguments"]?.jsonArray?.mapNotNull { it.toString() } ?: emptyList()
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

data class CommandData(
    val component: String,
    val capability: String,
    val command: String,
    val arguments: List<String>
)
