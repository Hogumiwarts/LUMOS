package com.hogumiwarts.data.entity.remote.Request.routine

data class RoutineCreateRequest(
    val routineName: String,
    val routineIcon: String,
    val devices: List<RoutineDeviceRequest>,
    val gestureId: Long?
)

data class RoutineDeviceRequest(
    val deviceId: Long,
    val commands: List<CommandRequest>?
)

data class CommandRequest(
    val component: String,
    val capability: String,
    val command: String,
    val arguments: List<Any>?
)