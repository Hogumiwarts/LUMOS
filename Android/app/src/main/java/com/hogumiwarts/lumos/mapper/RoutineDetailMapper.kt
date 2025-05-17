package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDeviceData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.routine.RoutineDetailData
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem

fun RoutineDetailData.toRoutineItem(): RoutineItem {
    return RoutineItem(
        routineName = routineName,
        routineIcon = routineIcon,
        gestureId = gestureId,
        gestureName = gestureName,
        gestureImageUrl = gestureImageUrl,
        gestureDescription = gestureDescription,
        devices = devices
    )
}

fun RoutineDeviceData.toRoutineDevice(): CommandDevice {
    return CommandDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = DeviceListType.from(deviceType).toString(),
        deviceImageUrl = null,
        commands = commands
    )
}

