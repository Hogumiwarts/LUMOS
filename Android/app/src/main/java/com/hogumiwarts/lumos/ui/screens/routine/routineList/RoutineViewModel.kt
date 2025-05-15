package com.hogumiwarts.lumos.ui.screens.routine.routineList

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RoutineViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RoutineState())
    val uiState: StateFlow<RoutineState> = _uiState

    // TODO: 루틴 로딩 로직 및 사용자 이벤트 처리 함수 추가
}