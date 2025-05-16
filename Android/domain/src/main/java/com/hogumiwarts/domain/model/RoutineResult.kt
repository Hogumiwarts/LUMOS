package com.hogumiwarts.domain.model

import kotlinx.serialization.json.JsonElement

sealed class RoutineResult {
    data class Success(
        val routines: List<Routine>
    ) : RoutineResult()

    data class Failure(val message: String) : RoutineResult()

    data class DetailSuccess(val detail: RoutineDetailData) : RoutineResult()

    object Unauthorized : RoutineResult()
}

data class Routine(
    val routineId: Int,
    val routineName: String,
    val routineIcon: String?,
    val gestureName: String?
)

// domain model
data class RoutineDetailData(
    val routineName: String,
    val routineIcon: String,
    val devices: List<CommandDevice>,
    val gestureId: Int,
    val gestureName: String,
    val gestureImageUrl: String,
    val gestureDescription: String
)

data class CommandDevice(
    val deviceId: Int,
    val deviceName: String,
    val deviceType: String,
    val deviceImageUrl: String?,
    val commands: List<CommandData>
)

data class CommandData(
    val component: String,
    val capability: String,
    val command: String,
    val arguments: List<Any>
)
