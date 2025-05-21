package com.hogumiwarts.data.mapper


import com.hogumiwarts.data.entity.remote.Response.routine.CommandDataResponse
import com.hogumiwarts.data.entity.remote.Response.routine.DeviceResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineCreateResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineData
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineEditResponse
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.routine.Routine
import com.hogumiwarts.domain.model.routine.RoutineCreateData
import com.hogumiwarts.domain.model.routine.RoutineDetail

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
                deviceId = device.deviceId,
                deviceName = device.deviceName,
                deviceType = device.deviceType,
                deviceImageUrl = device.deviceImageUrl ?: "", // null 방어
                commands = device.commands.map { command ->
                    CommandData(
                        component = command.component,
                        capability = command.capability,
                        command = command.command,
                        arguments = command.arguments ?: emptyList()
                    )
                }
            )
        }
    )
}


//fun CommandDevice.toRequest(): CommandDeviceRequest {
//    return CommandDeviceRequest(
//        deviceId = this.deviceId,
//        commands = this.commands.map {
//            CommandDataRequest(
//                component = it.component,
//                capability = it.capability,
//                command = it.command,
//                arguments = it.arguments ?: emptyList()
//            )
//        }
//    )
//}

fun RoutineEditResponse.toDomain(): RoutineDetail {
    return RoutineDetail(
        routineId = routineId,
        memberId = memberId,
        gestureId = gestureId,
        routineName = routineName,
        routineIcon = routineIcon,
        devices = devices.map { it.toDomain() }
    )
}


fun DeviceResponse.toDomain(): CommandDevice {
    return CommandDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = deviceType,
        deviceImageUrl = deviceImageUrl,
        commands = commands.map { it.toDomain() }
    )
}

fun CommandDataResponse.toDomain(): CommandData {
    return CommandData(
        component = component,
        capability = capability,
        command = command,
        arguments = arguments
    )
}
