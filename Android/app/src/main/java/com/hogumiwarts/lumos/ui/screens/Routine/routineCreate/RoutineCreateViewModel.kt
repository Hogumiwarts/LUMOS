package com.hogumiwarts.lumos.ui.screens.Routine.routineCreate

import androidx.lifecycle.ViewModel
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineDevice
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineIconType
import com.hogumiwarts.lumos.ui.screens.Routine.routineEdit.RoutineEditState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class RoutineCreateViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(RoutineEditState())
    val state: StateFlow<RoutineEditState> = _state

    private val _devices = MutableStateFlow<List<RoutineDevice>>(emptyList())
    val devices: StateFlow<List<RoutineDevice>> = _devices

    private val _selectedIcon =
        MutableStateFlow<RoutineIconType?>(null) // null 포함해서 처음에 아무것도 선택 안한 상태이도록
    val selectedIcon: StateFlow<RoutineIconType?> = _selectedIcon

    private val _routineName = MutableStateFlow("")
    val routineName: StateFlow<String> = _routineName

    fun selectIcon(icon: RoutineIconType) {
        _selectedIcon.value = icon
    }

    fun onRoutineNameChanged(newName: String) {
        _routineName.value = newName
    }

    fun loadInitialDevices(initial: List<RoutineDevice>) {
        _devices.value = initial
    }

    fun deleteDevice(device: RoutineDevice) {
        _devices.value = _devices.value.filterNot { it.deviceId == device.deviceId }
    }
}
