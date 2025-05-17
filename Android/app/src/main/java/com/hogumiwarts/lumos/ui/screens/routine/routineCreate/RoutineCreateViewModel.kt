package com.hogumiwarts.lumos.ui.screens.routine.routineCreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.routine.CreateRoutineParam
import com.hogumiwarts.domain.model.routine.RoutineResult
import com.hogumiwarts.domain.repository.RoutineRepository
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RoutineCreateViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(RoutineCreateState())
    val state: StateFlow<RoutineCreateState> = _state

    private val _devices = MutableStateFlow<List<CommandDevice>>(emptyList())
    val devices: StateFlow<List<CommandDevice>> = _devices

    private val _selectedIcon =
        MutableStateFlow<RoutineIconType?>(null) // null 포함해서 처음에 아무것도 선택 안한 상태이도록
    val selectedIcon: StateFlow<RoutineIconType?> = _selectedIcon

    private val _routineName = MutableStateFlow("")
    val routineName: StateFlow<String> = _routineName

    private val _gestureId = MutableStateFlow<Int?>(null)
    val gestureId: StateFlow<Int?> = _gestureId

    private val _selectedGesture = MutableStateFlow<GestureData?>(null)
    val selectedGesture: StateFlow<GestureData?> = _selectedGesture

    fun setGestureData(gesture: GestureData) {
        Timber.tag("gesture").d("🧩 setGestureData 호출됨: $gesture")

        _selectedGesture.value = gesture
        _gestureId.value = gesture.gestureId.toInt()
    }

    fun setGesture(id: Int) {
        _gestureId.value = id
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
        _state.update { it.copy(deviceEmptyMessage = message) }
    }

    fun clearDeviceError() {
        _state.update { it.copy(deviceEmptyMessage = null) }
    }

    // 루틴 생성 함수
    fun createRoutine(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val token = tokenDataStore.getAccessToken()

            val name = routineName.value
            val icon = selectedIcon.value?.name ?: ""
            val gesture = gestureId.value
            val deviceList = devices.value

            if (name.isBlank()) {
                setNameBlankError("루틴 이름은 필수 항목입니다.")
                return@launch
            }
            if (deviceList.isEmpty()) {
                setDeviceEmptyError("기기를 하나 이상 선택해주세요.")
                return@launch
            }

            //todo: 제스처 선택안할 수 있게 추후 변경
            if (gesture == null) {
                onError("제스처를 선택해주세요.")
                return@launch
            }

            val param = CreateRoutineParam(
                routineName = name,
                routineIcon = icon,
                gestureId = gesture,
                devices = deviceList
            )

            when (val result = routineRepository.createRoutine(param, token.toString())) {
                is RoutineResult.CreateSuccess -> onSuccess()
                is RoutineResult.Unauthorized -> onError("로그인 토큰이 만료되었습니다.")
                is RoutineResult.Failure -> onError(result.message)
                else -> onError("알 수 없는 오류 발생")
            }
        }
    }


}
