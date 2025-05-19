package com.hogumiwarts.lumos.ui.screens.devices


import android.annotation.SuppressLint
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
import android.devicelock.DeviceId
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult
import com.hogumiwarts.domain.model.audio.AudioPowerResult
import com.hogumiwarts.domain.repository.DeviceRepository
import com.hogumiwarts.domain.usecase.AirpurifierUseCase
import com.hogumiwarts.domain.usecase.AudioUseCase
import com.hogumiwarts.domain.usecase.LightUseCase
import com.hogumiwarts.domain.usecase.SwitchUseCase
import com.hogumiwarts.domain.usecase.TokensUseCase
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.mapper.toMyDevice
import com.hogumiwarts.lumos.ui.screens.gesture.network.sendTokenToWatch
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import timber.log.Timber
import kotlinx.serialization.json.*


@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val smartThingsApi: SmartThingsApi,
    private val deviceRepository: DeviceRepository,
    private val authApi: AuthApi,
    private val tokenDataStore: TokenDataStore,
    private val jwtUseCase: TokensUseCase,
    private val switchUseCase: SwitchUseCase,
    private val lightUseCase: LightUseCase,
    private val audioUseCase: AudioUseCase,
    private val airpurifierUseCase: AirpurifierUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val selectedDeviceId = mutableStateOf<Long?>(null)
    val showDialog = mutableStateOf(false)

    val clickDevice = mutableStateOf<MyDevice?>(null)


    private val _isLinked = MutableStateFlow(false) // SmartThings 계정 연동 여부
    val isLinked: StateFlow<Boolean> = _isLinked

    // 디바이스 목록
    private val _deviceList = MutableStateFlow<List<MyDevice>>(emptyList())
    val deviceList: StateFlow<List<MyDevice>> = _deviceList

    fun getJwt() {
        viewModelScope.launch {
            val a = jwtUseCase.getAccessToken().first()
            Log.d("TAG", "getJwt: $a")
            sendTokenToWatch(context, a)
        }

    }

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

            refreshDevicesFromDiscover(context) // backend api 통해서 불러옴
        }
    }


    // api 연동 확인 시 자동으로 기기 목록 호출
    fun checkAccountLinked() {
        viewModelScope.launch {
            tokenDataStore.getInstalledAppId().collect { id ->
                val isAvailable = id.isNotEmpty()
                Timber.tag("smartthings").d("📡 checkAccountLinked: id=$id → linked=$isAvailable")

                _isLinked.value = isAvailable
                if (isAvailable) loadDevicesFromServer() // 연동 확인되면 자동 호출됨
            }
        }
    }

    fun onDeviceClicked(device: MyDevice) {
        if (!device.isActive) {
//            showDialog.value = true
            clickDevice.value = device
        } else {
            //todo: 각 기기의 제어 화면으로 이동
            clickDevice.value = device

        }
    }

    fun dismissDialog() {
        showDialog.value = false
    }

    fun getSelectedDevice(devices: List<MyDevice>): MyDevice? {
        return devices.find { it.deviceId == selectedDeviceId.value }
    }


    // db에서 기기 목록 받아오기
    @SuppressLint("TimberArgTypes")
    fun loadDevicesFromServer() {
        viewModelScope.launch {
            try {
                val token = tokenDataStore.getRefreshToken().first()
                val result = deviceRepository.getDevicesFromServer(token)
                Timber.tag("DeviceList").d("🔐 사용한 토큰: Bearer $token")

                _deviceList.value = result.map { it.toMyDevice() } // 이전 목록 완전히 덮기

                Timber.tag("DeviceList").d("총 기기 수: ${result.size}")
                result.forEachIndexed { index, device ->
                    Timber.tag("DeviceLog").d(
                        "[%s] 🧩 id=%s, name=%s, type=%s, activated=%s",
                        index,
                        device.deviceId,
                        device.deviceName,
                        device.deviceType,
                        device.activated
                    )
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }


    // 기기 목록 새로고침
    @SuppressLint("TimberArgTypes")
    fun refreshDevicesFromDiscover(context: Context) {
        viewModelScope.launch {
            try {
                val accessToken = tokenDataStore.getAccessToken().first()
                val installedAppId = "5f810cf2-432c-4c4c-bc72-c5af5abf1ef5"

                //val installedAppId = tokenDataStore.getInstalledAppId().first()
                val newDevices = deviceRepository.discoverDevices(accessToken, installedAppId)

                Timber.tag("DeviceDiscover").d("🔄 Discover 기기 수: ${newDevices.size}")
                newDevices.forEachIndexed { index, device ->
                    Timber.tag("DeviceDiscover").d(
                        "[%d] 🛰️ id=%d, name=%s, type=%s, activated=%s",
                        index,
                        device.deviceId,
                        device.deviceName,
                        device.deviceType,
                        device.activated
                    )
                }

                val currentList = _deviceList.value
                val currentIds = currentList.map { it.deviceId }.toSet()

                val additional = newDevices
                    .filter { it.deviceId !in currentIds }
                    .map { it.toMyDevice() }

                _deviceList.value = currentList + additional

                //_deviceList.value = result.map { it.toMyDevice() }

                Toast.makeText(
                    context, "기기 목록 새로고침 완료 ✨" +
                            "", Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Timber.e(e, "❌ 기기 Discover 실패")
            }
        }
    }

    fun toggleDeviceState(deviceId: Long, deviceType: DeviceListType) {
        val currentList = _deviceList.value.toMutableList()

        val index = currentList.indexOfFirst { it.deviceId == deviceId }
        if (index != -1) {
            val target = currentList[index]
            // 서버랑 호출
            viewModelScope.launch {

                when (deviceType) {
                    DeviceListType.AIRPURIFIER -> {
                        val result = airpurifierUseCase.patchAirpurifierPower(
                            deviceId = deviceId.toLong(),
                            !target.isOn
                        )
                        when (result) {
                            is PatchAirpurifierPowerResult.Error -> {
                                // TODO: 에러 처리
                            }

                            is PatchAirpurifierPowerResult.Success -> {
                                val updated = target.copy(isOn = result.data.activated) // isOn 토글
                                currentList[index] = updated
                                _deviceList.value = currentList
                            }
                        }
                    }

                    DeviceListType.LIGHT -> {
                        val result =
                            lightUseCase.patchLightPower(deviceId = deviceId.toLong(), !target.isOn)
                        when (result) {
                            is PatchSwitchPowerResult.Error -> {
                                // TODO: 에러 처리
                            }

                            is PatchSwitchPowerResult.Success -> {
                                val updated = target.copy(isOn = result.data.activated) // isOn 토글
                                currentList[index] = updated
                                _deviceList.value = currentList
                            }
                        }

                    }

                    DeviceListType.AUDIO -> {
                        val result =
                            audioUseCase.patchAudioPower(deviceId = deviceId.toLong(), !target.isOn)
                        when (result) {
                            is AudioPowerResult.Error -> {
                                // TODO: 에러 처리
                            }

                            is AudioPowerResult.Success -> {
                                val updated = target.copy(isOn = result.data.activated) // isOn 토글
                                currentList[index] = updated
                                _deviceList.value = currentList
                            }
                        }
                    }

                    DeviceListType.SWITCH -> {
                        val result = switchUseCase.patchSwitchStatus(
                            deviceId = deviceId.toLong(),
                            !target.isOn
                        )
                        when (result) {
                            is PatchSwitchPowerResult.Error -> {
                                // TODO: 에러 처리
                            }

                            is PatchSwitchPowerResult.Success -> {
                                val updated = target.copy(isOn = result.data.activated) // isOn 토글
                                currentList[index] = updated
                                _deviceList.value = currentList
                            }
                        }
                    }

                    DeviceListType.ETC -> {}
                }


            }


            val updated = target.copy(isOn = !target.isOn) // isOn 토글
            currentList[index] = updated
            _deviceList.value = currentList
        }
    }

    private fun observeTokenChanges() {
        viewModelScope.launch {
            tokenDataStore.getInstalledAppId().collect { id ->
                _isLinked.value = id.isNotEmpty()
            }
        }
    }
}
