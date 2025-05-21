package com.hogumiwarts.data.entity.remote.Response.routine

data class RoutineResponse(
    val status: Long,
    val message: String,
    val data: List<RoutineData>
)

data class RoutineData(
    val routineId: Long,
    val routineName: String,
    val routineIcon: String,
    val gestureName: String
)