package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDeviceData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.routine.RoutineDetailData
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem

fun RoutineDetailData.toRoutineItem(routineId: Int): RoutineItem {
    return RoutineItem(
        routineId = routineId,
        routineName = routineName,
        routineIcon = routineIcon,
        gestureId = gestureId,
        gestureName = gestureName,
        gestureImageUrl = gestureImageUrl,
        gestureDescription = gestureDescription,
        devices = devices
    )
}

