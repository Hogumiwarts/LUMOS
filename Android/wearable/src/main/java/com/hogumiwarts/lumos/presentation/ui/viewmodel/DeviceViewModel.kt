package com.hogumiwarts.lumos.presentation.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.usecase.DeviceUseCase
import com.hogumiwarts.domain.usecase.TokensUseCase
import com.hogumiwarts.lumos.domain.model.GetDevicesResult
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceIntent
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase, // 유즈케이스 주입
    private val jwtUseCase: TokensUseCase,
    @ApplicationContext private val context: Context, // 앱 context (현재는 미사용)
) : ViewModel() {

    // 🔹 상태(State)를 담는 StateFlow (Idle, Loading, Loaded, Error)
    private val _state = MutableStateFlow<DeviceState>(DeviceState.Idle)
    val state: StateFlow<DeviceState> = _state

    // 🔸 Intent를 받기 위한 SharedFlow (MVI 이벤트 트리거
    val intentFlow = MutableSharedFlow<DeviceIntent>()

    init {
        handleIntent() // Intent 처리 루프 시작
    }

    // 🔄 화면에서 보낸 Intent 수신 및 처리
    private fun handleIntent() {
        viewModelScope.launch {
            intentFlow.collectLatest { intent ->
                when (intent) {
                    DeviceIntent.LoadDevice,DeviceIntent.Refresh -> loadDevices()
                }
            }
        }
    }

    // 🔸 외부에서 Intent를 보낼 수 있도록 helper 함수 제공
    fun sendIntent(intent: DeviceIntent) {
        viewModelScope.launch {
            intentFlow.emit(intent)
        }
    }

    fun saveJwt(accessToken: String, refreshToken: String){
        viewModelScope.launch {
            jwtUseCase.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
        }

    }
    private val _tokenState = MutableStateFlow<TokenState>(TokenState.Loading)
    val tokenState: StateFlow<TokenState> = _tokenState

    fun getAccess(){
        // Flow에서 첫 번째 값을 비동기적으로 수집
        viewModelScope.launch {
            val token = jwtUseCase.getAccessToken().first()
            _tokenState.value = TokenState.Loaded(token)
        }
    }


    // 🔁 실제 비즈니스 로직 실행: 기기 데이터 불러오기
    private fun loadDevices() {
        viewModelScope.launch {
            _state.value = DeviceState.Loading

            when (val result = deviceUseCase.getDevice()) {

                is com.hogumiwarts.domain.model.devices.GetDevicesResult.Error -> {
                    _state.value = DeviceState.Error(result.error)
                }
                is com.hogumiwarts.domain.model.devices.GetDevicesResult.Success -> {
                    _state.value = DeviceState.Loaded(result.data)

                }
            }
        }
    }
}
