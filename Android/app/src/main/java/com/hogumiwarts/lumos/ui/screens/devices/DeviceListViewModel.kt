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
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.mapper.toMyDevice
import timber.log.Timber
import kotlinx.serialization.json.*


@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val smartThingsApi: SmartThingsApi
) : ViewModel() {
    @Inject
    lateinit var tokenDataStore: TokenDataStore

    val selectedDeviceId = mutableStateOf<String?>(null)
    val showDialog = mutableStateOf(false)

    private val _isLinked = MutableStateFlow(false) // SmartThings 계정 연동 여부
    val isLinked: StateFlow<Boolean> = _isLinked

    // 디바이스 목록
    private val _deviceList = MutableStateFlow<List<MyDevice>>(emptyList())
    val deviceList: StateFlow<List<MyDevice>> = _deviceList


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
        fetchDevicesWithStatus()
    }

    fun checkAccountLinked() {
        viewModelScope.launch {
            tokenDataStore.getInstalledAppId().collect { id ->
                val isAvailable = id.isNotEmpty()
                _isLinked.value = isAvailable
                if (isAvailable) fetchDevicesWithStatus() // 연동 확인되면 자동 호출됨
            }
        }
    }

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

    // 상태 조회
    fun fetchDevicesWithStatus() {
        viewModelScope.launch {
            try {
                //todo: 현재 테스트 폰 smartthings에 연결 기기가 없어서 임시로 하드 코딩함

                // val installedAppId = tokenDataStore.getInstalledAppId().first()
                val installedAppId = "5f810cf2-432c-4c4c-bc72-c5af5abf1ef5"
                val response = smartThingsApi.getDeviceList(installedAppId)

                Timber.tag("SmartThings").d("✅ installedAppId = $installedAppId")
                Timber.tag("SmartThings").d("✅ devices response = ${response.devices.size}")

                if (response.success) {
                    val devices = response.devices

                    val enrichedDevices = devices.map { device ->
                        try {
                            val statusResponse =
                                smartThingsApi.getDeviceStatus(device.deviceId, installedAppId)
                            if (statusResponse.success) {
                                val mainComponent = statusResponse.status.components["main"]

                                // 기기별 카테고리
                                val category = device.components.firstOrNull()?.categories?.firstOrNull()?.name.orEmpty()

                                // isOn과 isActive의 경우 JSON에서 바로 알 수 없어서 따로 판단해줌
                                // todo: 스피커 json 확인하고 마저 처리하기
                                val isOn = when (category) {
                                    "AirPurifier" -> mainComponent?.custom_airPurifierOperationMode?.apOperationMode?.value != "off"
                                    "Switch", "Light" -> mainComponent?.switch?.switch?.value == "on"
                                    "Hub" -> true
                                    
                                    else -> false
                                }


                                val isActive = when (category) {
                                    "Hub" -> true // Hub는 상태 체크 불필요
                                    else ->  mainComponent?.healthCheck?.`DeviceWatch-DeviceStatus`?.value == "online"
                                }


                                device.toMyDevice(isOn, isActive)
                            } else {
                                device.toMyDevice(isOn = false, isActive = false)
                            }
                        } catch (e: Exception) {
                            Timber.e(e, "기기 상태 조회 실패")
                            device.toMyDevice(isOn = false, isActive = false)
                        }
                    }

                    _deviceList.value = enrichedDevices
                }
            } catch (e: Exception) {
                Timber.tag("SmartThings").e(e, "⚠️ 기기 목록 가져오기 실패")
            }
        }
    }

}
