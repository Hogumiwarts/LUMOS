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
import timber.log.Timber
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

    private val _isInitialized = MutableStateFlow(false)

    fun isInitialized() = _isInitialized.value

    fun loadInitialDevicesOnce(initial: List<CommandDevice>) {
        if (_isInitialized.compareAndSet(expect = false, update = true)) {
            Timber.d("🔰 초기 기기 로드됨: $initial")
            _devices.value = initial
        } else {
            Timber.d("⚠️ 이미 초기화된 상태이므로 무시")
        }
    }

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

    fun addDevice(device: CommandDevice) {
        val current = _devices.value
        if (current.none { it.deviceId == device.deviceId }) {
            _devices.value = current + device
        }
    }

    fun updateDevice(updated: CommandDevice) {
        Timber.d("🛠 updateDevice 호출됨: $updated")
        _devices.update { list ->
            val newList = list.map {
                if (it.deviceId == updated.deviceId) updated else it
            }
            _devices.value = newList.toList()
            Timber.d("📋 업데이트된 기기 리스트: $newList")
            newList
        }
    }

}