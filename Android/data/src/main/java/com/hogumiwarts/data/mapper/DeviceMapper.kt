package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDetailData
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDeviceData
import com.hogumiwarts.domain.model.CommandData
import com.hogumiwarts.domain.model.CommandDevice
import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.model.RoutineDetail
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

fun RoutineDetailData.toDomain(): com.hogumiwarts.domain.model.RoutineDetailData {
    return com.hogumiwarts.domain.model.RoutineDetailData(
        routineName = routineName,
        routineIcon = routineIcon,
        gestureId = gestureId,
        gestureName = gestureName,
        gestureImageUrl = gestureImageUrl,
        gestureDescription = gestureDescription,
        devices = devices.map { it.toRoutineDevice() }
    )
}

fun RoutineDeviceData.toDomain(): CommandDevice {
    return CommandDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = deviceType,
        deviceImageUrl = deviceImageUrl,
        commands = parseCommands(commands) // 이미 만든 JsonElement 파서 활용
    )
}

fun RoutineDeviceData.toRoutineDevice(): com.hogumiwarts.domain.model.RoutineDeviceData {
    return com.hogumiwarts.domain.model.RoutineDeviceData(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = deviceType,
        deviceImageUrl = deviceImageUrl,
        commands = commands
    )
}


