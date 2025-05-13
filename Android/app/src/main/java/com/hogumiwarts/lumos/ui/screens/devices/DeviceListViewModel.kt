package com.hogumiwarts.lumos.ui.screens.devices


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.data.source.remote.SmartThingsApi
import com.hogumiwarts.lumos.ui.common.MyDevice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.hogumiwarts.data.entity.remote.Response.SmartThingsAuthResponse
import com.hogumiwarts.data.entity.remote.Response.SmartThingsDevice
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import kotlinx.coroutines.launch
import timber.log.Timber


@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val smartThingsApi: SmartThingsApi
) : ViewModel() {
    @Inject
    lateinit var tokenDataStore: TokenDataStore

    val selectedDeviceId = mutableStateOf<Int?>(null)
    val showDialog = mutableStateOf(false)

    private val _isLinked = MutableStateFlow(false) // SmartThings 계정 연동 여부
    val isLinked: StateFlow<Boolean> = _isLinked

    // 디바이스 목록
    private val _deviceList = MutableStateFlow<List<SmartThingsDevice>>(emptyList())
    val deviceList: StateFlow<List<SmartThingsDevice>> = _deviceList

    // SmartThings 인증 URL 요청 및 브라우저 이동 함수
    fun requestAuthAndOpen(context: Context) {
        viewModelScope.launch {
            try {
                val authUrl = smartThingsApi.getSmartThingsAuthUrl().url
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(authUrl))
                context.startActivity(intent)
            } catch (e: Exception) {
                Timber.tag("SmartThings").e(e, "⚠\uFE0F 인증 URL 요청 실패: " + e.message)
            }
        }
    }

    fun fetchDevices() {
        viewModelScope.launch {
            tokenDataStore.getInstalledAppId().collect { installedAppId ->
                try {
                    val response = smartThingsApi.getDeviceList(installedAppId)
                    if (response.success) {
                        _deviceList.value = response.devices
                    }
                } catch (e: Exception) {
                    Timber.tag("SmartThings").e(e, "⚠️ 기기 목록 가져오기 실패")
                }
            }
        }
    }

    fun checkAccountLinked() {
        viewModelScope.launch {
            tokenDataStore.getInstalledAppId().collect { id ->
                val isAvailable = id.isNotEmpty()
                _isLinked.value = isAvailable
                if (isAvailable) fetchDevices() // 연동 확인되면 자동 호출됨
            }
        }
//        _isLinked.value = linkCheckLogic()
    }

    // 임시
//    private fun linkCheckLogic(): Boolean {
//        //todo: 임시로 연동되어 있지 않다고 가정 -> 테스트 시 아래 boolean 값 바꾸면 됩니당
//        return false
//    }

    fun onDeviceClicked(device: MyDevice) {
        if (!device.isActive) {
            showDialog.value = true
        } else {
            //todo: 각 기기의 제어 화면으로 이동
        }
    }

    fun dismissDialog() {
        showDialog.value = false
    }

    fun getSelectedDevice(devices: List<MyDevice>): MyDevice? {
        return devices.find { it.deviceId == selectedDeviceId.value }
    }
}
