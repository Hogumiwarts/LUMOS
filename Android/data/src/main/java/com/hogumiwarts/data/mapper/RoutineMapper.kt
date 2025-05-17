package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.routine.RoutineCreateResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineData
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.routine.Routine
import com.hogumiwarts.domain.model.routine.RoutineCreateData

fun List<RoutineData>.toDomain(): List<Routine> {
    return this.map {
        Routine(
            routineId = it.routineId,
            routineName = it.routineName,
            routineIcon = it.routineIcon,
            gestureName = it.gestureName
        )
    }
}

fun RoutineCreateResponse.toDomain(): RoutineCreateData {
    return RoutineCreateData(
        routineId = routineId,
        routineName = routineName,
        routineIcon = routineIcon,
        gestureId = gestureId,
        devices = devices.map { device ->
            CommandDevice(
                deviceId = device.deviceId.toString(),
                deviceName = device.deviceName,
                deviceType = device.deviceType,
                deviceImageUrl = device.deviceImageUrl,
                commands = device.commands.map { command ->
                    CommandData(
                        component = command.component,
                        capability = command.capability,
                        command = command.command,
                        arguments = command.arguments
                    )
                }
            )
        }
    )
}