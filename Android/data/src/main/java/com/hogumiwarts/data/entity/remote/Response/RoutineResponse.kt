package com.hogumiwarts.data.entity.remote.Response

data class RoutineResponse(
    val status: Int,
    val message: String,
    val data: RoutineData
)

data class RoutineData(
    val routineId: Int,
    val routineName: String,
    val routineIcon: String,
    val gestureName: String
)