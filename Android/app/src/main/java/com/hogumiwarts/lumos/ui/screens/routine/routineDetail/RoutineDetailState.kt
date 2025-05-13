package com.hogumiwarts.lumos.ui.screens.routine.routineDetail

import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem

sealed class RoutineDetailState {
    object Loading : RoutineDetailState()
    data class Success(val routine: RoutineItem, val devices: List<RoutineDevice>) : RoutineDetailState()
    data class Error(val message: String) : RoutineDetailState()
}