package com.hogumiwarts.lumos.ui.screens.routine.routineCreate

import androidx.lifecycle.ViewModel
import com.hogumiwarts.domain.model.CommandDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RoutineCreateViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(RoutineCreateState())
    val state: StateFlow<RoutineCreateState> = _state

    private val _devices = MutableStateFlow<List<CommandDevice>>(emptyList())
    val devices: StateFlow<List<CommandDevice>> = _devices

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

    fun loadInitialDevices(initial: List<CommandDevice>) {
        _devices.value = initial
    }

    fun deleteDevice(device: CommandDevice) {
        _devices.value = _devices.value.filterNot { it.deviceId == device.deviceId }
    }

    fun addDevice(device: CommandDevice) {
        _devices.value = _devices.value + device
    }

    fun setNameBlankError(message: String) {
        _state.value = state.value.copy(nameBlankMessage = message)
    }

    fun clearNameError() {
        _state.value = state.value.copy(nameBlankMessage = null)
    }

    fun setDeviceEmptyError(message: String?) {
        _state.update { it.copy(deviceEmptyMessage  = message) }
    }

    fun clearDeviceError() {
        _state.update { it.copy(deviceEmptyMessage = null) }
    }

}
