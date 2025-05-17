package com.hogumiwarts.data.entity.remote.Response.routine

data class RoutineCreateResponse(
    val routineId: Int,
    val memberId: Int,
    val gestureId: Int,
    val routineName: String,
    val routineIcon: String,
    val devices: List<RoutineDeviceData>
)