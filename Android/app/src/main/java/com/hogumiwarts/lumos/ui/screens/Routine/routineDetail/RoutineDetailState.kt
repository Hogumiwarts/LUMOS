package com.hogumiwarts.lumos.ui.screens.Routine.routineDetail

import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineDevice
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineItem

sealed class RoutineDetailState {
    object Loading : RoutineDetailState()
    data class Success(val routine: RoutineItem, val devices: List<RoutineDevice>) : RoutineDetailState()
    data class Error(val message: String) : RoutineDetailState()
}