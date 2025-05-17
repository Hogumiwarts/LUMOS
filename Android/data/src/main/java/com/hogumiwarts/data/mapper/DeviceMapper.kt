package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDetailData
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDeviceData
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.DeviceResult
import kotlinx.serialization.json.Json
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

fun parseCommands(commands: String): List<CommandData> {
    return try {
        val element = Json.parseToJsonElement(commands)
        val commandArray = element.jsonArray
        commandArray.map { obj ->
            val commandObj = obj.jsonObject
            CommandData(
                component = commandObj["component"]?.jsonPrimitive?.content.orEmpty(),
                capability = commandObj["capability"]?.jsonPrimitive?.content.orEmpty(),
                command = commandObj["command"]?.jsonPrimitive?.content.orEmpty(),
                arguments = commandObj["arguments"]?.jsonArray?.map { it.toString() } ?: emptyList()
            )
        }
    } catch (e: Exception) {
        emptyList()
    }
}

fun RoutineDetailData.toDomain(): com.hogumiwarts.domain.model.routine.RoutineDetailData {
    return com.hogumiwarts.domain.model.routine.RoutineDetailData(
        routineName = routineName,
        routineIcon = routineIcon,
        gestureId = gestureId,
        gestureName = gestureName,
        gestureImageUrl = gestureImageUrl,
        gestureDescription = gestureDescription,
        devices = devices.map { it.toDomain() }
    )
}

fun RoutineDeviceData.toDomain(): CommandDevice {
    return CommandDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = deviceType,
        deviceImageUrl = deviceImageUrl,
        commands = commands
    )
}

