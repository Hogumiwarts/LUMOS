package com.hogumiwarts.lumos.ui.screens.routine.routineEdit

import android.util.Log
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
    val isInitialized: Boolean get() = _isInitialized.value

    fun setInitialized(initialized: Boolean) {
        _isInitialized.value = initialized
    }


    fun setNameBlankError(message: String) {
        _state.value = state.value.copy(nameBlankMessage = message)
    }

    fun clearNameError() {
        _state.value = state.value.copy(nameBlankMessage = null)
    }

    fun setDeviceEmptyError(message: String?) {
        _state.update { it.copy(deviceEmptyMessage = message) }
    }

    fun loadInitialDevicesOnce(initial: List<CommandDevice>) {
        if (!_isInitialized.value) {
            Timber.d("🔰 초기 기기 로드됨: $initial")
            _devices.value = initial
            _isInitialized.value = true
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

    // 초기화 플래그를 수정하는 새로운 메서드
    fun resetInitializationState() {
        _isInitialized.value = false
    }

    // 기기 목록을 완전히 대체하는 메서드
    fun replaceDevices(devices: List<CommandDevice>) {
        _devices.value = devices
        Timber.d("🔄 기기 목록 대체 완료: ${devices.size}개")
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
        _devices.update { currentList ->
            if (currentList.none { it.deviceId == device.deviceId }) {
                Log.d("TAG", "addDevice: ${currentList + device}")
                currentList + device
            } else currentList
        }
    }

    fun updateDevice(updated: CommandDevice) {
        Timber.d("🛠 updateDevice 호출됨: $updated")
        _devices.update { list ->
            val newList = list.map {
                if (it.deviceId == updated.deviceId) updated else it
            }
//            _devices.value = newList.toList()
            Timber.d("📋 업데이트된 기기 리스트: $newList")
            newList
        }
    }

    // 임시 기기 목록 백업 - 기기 컨트롤 화면으로 이동 전에 저장
    private var _tempDevicesList: List<CommandDevice> = emptyList()


    fun backupCurrentDevices() {
        if (_devices.value.isNotEmpty()) {
            _tempDevicesList = _devices.value.toList()
            Timber.d("📦 현재 기기 목록 백업: ${_tempDevicesList.size}개")
        }
    }

    // 항상 백업을 확인하는 메서드
    fun checkAndRestoreDevices() {
        if (_tempDevicesList.isNotEmpty()) {
            val currentDevices = _devices.value
            val combinedDevices = (currentDevices + _tempDevicesList).distinctBy { it.deviceId }
            _devices.value = combinedDevices
            Timber.d("🔄 기기 목록 확인 및 복원: ${_devices.value.size}개")
        }
    }

}