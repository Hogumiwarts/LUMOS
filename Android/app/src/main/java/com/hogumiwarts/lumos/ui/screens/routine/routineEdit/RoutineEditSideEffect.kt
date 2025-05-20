package com.hogumiwarts.lumos.ui.screens.routine.routineEdit

// 사용자에게 보여줄 사이드 effect(토스트, 완료 등)
sealed class RoutineEditSideEffect {
    object EditComplete : RoutineEditSideEffect()
    data class ShowToast(val message: String) : RoutineEditSideEffect()
}
