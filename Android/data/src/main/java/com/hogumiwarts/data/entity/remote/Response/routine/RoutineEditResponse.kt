package com.hogumiwarts.data.entity.remote.Response.routine

data class RoutineEditResponse(
    val routineId: Long,
    val memberId: Long,
    val gestureId: Int?,
    val routineName: String,
    val routineIcon: String,
    val devices: List<DeviceResponse>
)

data class DeviceResponse(
    val deviceId: Long,
    val deviceName: String,
    val deviceType: String,
    val deviceImageUrl: String,
    val commands: List<CommandDataResponse>
)

data class CommandDataResponse(
    val component: String,
    val capability: String,
    val command: String,
    val arguments: List<Any> = emptyList()
)
