package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.domain.model.RoutineDetailData
import com.hogumiwarts.domain.model.RoutineDeviceData
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineDevice

fun RoutineDetailData.toRoutineItem(): RoutineItem {
    return RoutineItem(
        routineName = routineName,
        routineIcon = routineIcon,
        gestureId = gestureId,
        gestureName = gestureName,
        gestureImageUrl = gestureImageUrl,
        gestureDescription = gestureDescription,
        devices = devices.map { it.toRoutineDevice() }
    )
}

fun RoutineDeviceData.toRoutineDevice(): RoutineDevice {
    return RoutineDevice(
        deviceId = deviceId.toString(),
        deviceName = deviceName,
        deviceType = DeviceListType.from(deviceType),
        isOn = true
    )
}
