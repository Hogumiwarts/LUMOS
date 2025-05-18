package com.hogumiwarts.lumos.ui.screens.routine.routineEdit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.data.source.remote.RoutineApi
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.routine.CreateRoutineParam
import com.hogumiwarts.domain.model.routine.RoutineResult
import com.hogumiwarts.domain.repository.RoutineRepository
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineEditViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {
    private val _state = MutableStateFlow(RoutineEditState())
    val state: StateFlow<RoutineEditState> = _state

    private val _devices = MutableStateFlow<List<CommandDevice>>(emptyList())
    val devices: StateFlow<List<CommandDevice>> = _devices

    private val _editResult = MutableStateFlow<RoutineResult?>(null)
    val editResult: StateFlow<RoutineResult?> = _editResult

    private val _selectedIcon =
        MutableStateFlow<RoutineIconType?>(null) // null 포함해서 처음에 아무것도 선택 안한 상태이도록
    val selectedIcon: StateFlow<RoutineIconType?> = _selectedIcon

    private val _routineName = MutableStateFlow("")
    val routineName: StateFlow<String> = _routineName

    private val _routineId = MutableStateFlow<Long>(-1L)
    val routineId: StateFlow<Long> = _routineId

    private val _gestureId = MutableStateFlow<Long?>(null)
    val gestureId: StateFlow<Long?> = _gestureId

    private val _selectedGesture = MutableStateFlow<GestureData?>(null)
    val selectedGesture: StateFlow<GestureData?> = _selectedGesture


    fun selectIcon(icon: RoutineIconType) {
        _selectedIcon.value = icon
    }

    fun onRoutineNameChanged(newName: String) {
        _routineName.value = newName
    }

    fun loadInitialDevices(initial: List<CommandDevice>) {
        _devices.value = initial
    }

    fun deleteDevice(device: CommandDevice) {
        _devices.update { list -> list.filterNot { it.deviceId == device.deviceId } }
    }

    // api 연동 함수
    fun updateRoutine(
        routineId: Long,
        accessToken: String,
        gestureId: Long? = null
    ) {
        viewModelScope.launch {
            val param = CreateRoutineParam(
                routineName = _routineName.value,
                routineIcon = _selectedIcon.value?.iconName ?: "default",
                gestureId = gestureId,
                devices = _devices.value
            )

            val result = routineRepository.updateRoutine(routineId, param, accessToken)
            _editResult.value = result
        }
    }

    fun setRoutineId(id: Long?) {
        _routineId.value = id ?: -1L
    }

    fun setGestureData(data: GestureData?) {
        _selectedGesture.value = data
        _gestureId.value = data?.gestureId
    }
}