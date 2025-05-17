package com.hogumiwarts.lumos.ui.screens.routine.routineDetail

import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem

sealed class RoutineDetailState {
    object Loading : RoutineDetailState()
    data class Success(val routine: RoutineItem, val devices: List<CommandDevice>) : RoutineDetailState()
    data class Error(val message: String) : RoutineDetailState()
}