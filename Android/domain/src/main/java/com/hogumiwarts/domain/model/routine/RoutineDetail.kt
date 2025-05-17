package com.hogumiwarts.domain.model.routine

// domain 모듈 - 뷰모델/화면에서 사용하는 내부 표현
data class RoutineDetail(
    val routineName: String,
    val routineIcon: String,
    val devices: List<CommandDevice>,
)
