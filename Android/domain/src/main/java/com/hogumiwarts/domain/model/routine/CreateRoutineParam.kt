package com.hogumiwarts.domain.model.routine

data class CreateRoutineParam(
    val routineName: String,
    val routineIcon: String,
    val gestureId: Long?,
    val devices: List<CommandDevice>
)
