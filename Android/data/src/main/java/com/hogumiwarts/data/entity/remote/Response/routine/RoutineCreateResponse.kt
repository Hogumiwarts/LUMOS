package com.hogumiwarts.data.entity.remote.Response.routine

data class RoutineCreateResponse(
    val routineId: Long,
    val memberId: Int,
    val gestureId: Long,
    val routineName: String,
    val routineIcon: String,
    val devices: List<RoutineDeviceData>
)