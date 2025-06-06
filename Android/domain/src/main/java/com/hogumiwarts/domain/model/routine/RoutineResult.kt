package com.hogumiwarts.domain.model.routine

sealed class RoutineResult {
    data class Success(
        val routines: List<Routine>
    ) : RoutineResult()

    data class Failure(val message: String) : RoutineResult()

    data class DetailSuccess(val detail: RoutineDetailData) : RoutineResult()

    data class CreateSuccess(val routine: RoutineCreateData) : RoutineResult()

    data class EditSuccess(val data: RoutineDetail) : RoutineResult()

    object DeleteSuccess : RoutineResult()

    object Unauthorized : RoutineResult()
}


data class Routine(
    val routineId: Long,
    val routineName: String,
    val routineIcon: String?,
    val gestureName: String?
)

// domain model
data class RoutineDetailData(
    val routineName: String,
    val routineIcon: String,
    val devices: List<CommandDevice>,
    val gestureId: Long?,
    val gestureName: String?,
    val gestureImageUrl: String?,
    val gestureDescription: String?
)

data class CommandDevice(
    val deviceId: Long,
    val deviceName: String,
    val deviceType: String,
    val deviceImageUrl: String?,
    val commands: List<CommandData>
)

data class CommandData(
    val component: String,
    val capability: String,
    val command: String,
    val arguments: List<@JvmSuppressWildcards Any>? = emptyList() // nullable 또는 기본값
)

data class RoutineCreateData(
    val routineId: Long,
    val routineName: String,
    val routineIcon: String,
    val gestureId: Long,
    val devices: List<CommandDevice>
)
