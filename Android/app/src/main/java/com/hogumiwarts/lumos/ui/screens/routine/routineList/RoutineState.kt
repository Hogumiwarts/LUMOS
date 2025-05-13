package com.hogumiwarts.lumos.ui.screens.routine.routineList

import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem

// TODO: 루틴 화면의 UI 상태를 정의할 데이터 클래스 작성
data class RoutineState (
    val routines: List<RoutineItem> = emptyList(),
    val isLoading: Boolean = false
)