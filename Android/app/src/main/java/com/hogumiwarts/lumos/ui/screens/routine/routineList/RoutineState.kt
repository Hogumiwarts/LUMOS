package com.hogumiwarts.lumos.ui.screens.routine.routineList

import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem

data class RoutineState (
    val routines: List<RoutineItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)