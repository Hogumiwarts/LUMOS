package com.hogumiwarts.lumos.ui.screens.Routine.routineDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineDevice
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineDetailViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<RoutineDetailState>(RoutineDetailState.Loading)
    val state: StateFlow<RoutineDetailState> = _state

    fun loadRoutine(routineId: String?) {
        viewModelScope.launch {
            //todo: 실제 api 연동
            val routine = RoutineItem.sample.find { it.id.toString() == routineId }
            val devices = RoutineDevice.sample

            if (routine != null) {
                _state.value = RoutineDetailState.Success(routine, devices)
            } else {
                _state.value = RoutineDetailState.Error("\uD83D\uDE22 루틴 정보를 찾을 수 없습니다!")
            }
        }
    }

    fun setError(message: String) {
        _state.value = RoutineDetailState.Error(message)
    }

}