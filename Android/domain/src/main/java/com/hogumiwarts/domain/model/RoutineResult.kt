package com.hogumiwarts.domain.model

sealed class RoutineResult {
    data class Success(
        val routines: List<Routine>
    ) : RoutineResult()

    data class Failure(val message: String) : RoutineResult()

    object Unauthorized : RoutineResult()
}

data class Routine(
    val routineId: Int,
    val routineName: String,
    val routineIcon: String?,
    val gestureName: String
)