package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.device.GetDevicesResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDetailData
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDeviceData
import com.hogumiwarts.domain.model.ControlData
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.model.devices.DeviceListData
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
        deviceId = deviceId.toString(),
        deviceName = deviceName,
        deviceType = deviceType,
        deviceImageUrl = deviceImageUrl,
        commands = commands
    )
}

object DeviceMapper {

    // 🔄 전체 리스트 변환 함수: DTO 리스트 → 도메인 모델 리스트
    fun fromDeviceListDataResponseList(dtoList: List<GetDevicesResponse>): List<DeviceListData> {
        return dtoList.map { fromDeviceListDataResponse(it) }
    }

    // 🔄 단일 DTO 변환 함수: DTO → 도메인 모델
    fun fromDeviceListDataResponse(response: GetDevicesResponse): DeviceListData {
        return DeviceListData(
            deviceId = response.deviceId,
            tagNumber = response.tagNumber,
            installedAppId = response.installedAppId,
            deviceImg = response.deviceImg,
            deviceName = response.deviceName,
            deviceType = response.deviceType,
            activated = response.activated
        )
    }

    fun fromSwitchPowerResponse(dtoList: PatchControlResponse): ControlData {
        return ControlData(
            success = dtoList.success,
            activated = dtoList.activated
        )
    }
}

