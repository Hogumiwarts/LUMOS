package com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.repository.DeviceRepository
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.mapper.toMyDevice
import com.hogumiwarts.lumos.ui.common.MyDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class RoutineDeviceListViewModel @Inject constructor(
    private val deviceRepository: DeviceRepository,
    private val tokenDataStore: TokenDataStore
) : ViewModel() {
    val selectedDeviceId = mutableStateOf<Int?>(null)
    val showDialog = mutableStateOf(false)
    val devices = mutableStateOf<List<MyDevice>>(emptyList())

    init {
        loadDevices()
    }

    private fun loadDevices() {
        viewModelScope.launch {
            try {
                val token = tokenDataStore.getRefreshToken().first()
                val result = deviceRepository.getDevicesFromServer(token)
                devices.value = result.map { it.toMyDevice() }
            } catch (e: Exception) {
                Timber.e(e, "❌ 기기 불러오기 실패")
            }
        }
    }

    fun onDeviceClicked(device: MyDevice) {
        // 비활성화 상태 무시
        selectedDeviceId.value =
            if (selectedDeviceId.value == device.deviceId) null else device.deviceId

    }

    fun dismissDialog() {
        showDialog.value = false
    }

    fun getSelectedDevice(): MyDevice? {
        return devices.value.find { it.deviceId == selectedDeviceId.value }
    }

    fun clearSelectedDevice() {
        selectedDeviceId.value = null
    }


}