package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

// 전체 로그인 여부 관리
@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenDataStore: TokenDataStore,
    private val authRepository: AuthRepository,
    private val authApi: AuthApi
) : ViewModel() {

    val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    val _isSignup = MutableStateFlow<Boolean?>(null)
    val isSignup: StateFlow<Boolean?> = _isSignup

    init {
        viewModelScope.launch {
            val token = tokenDataStore.getAccessToken().first()
            _isLoggedIn.value = token.isNotEmpty()
        }
    }

    // 로그인
    fun logIn() {
        _isLoggedIn.value = true
    }

    // 로그아웃
    fun logOut(onSuccess: () -> Unit = {}, onFailure: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val accessToken = tokenDataStore.getAccessToken().first()
                val refreshToken = tokenDataStore.getRefreshToken().first()

                // 1. 로그아웃 시도
                val logoutSuccess = authRepository.logout(accessToken)

                if (logoutSuccess) {
                    tokenDataStore.clearTokens()
                    _isLoggedIn.value = false
                    onSuccess()
                } else {
                    Timber.tag("auth").d("⚠️ 로그아웃 실패 → 토큰 갱신 시도")

                    // 2. 토큰 갱신 시도
                    try {
                        val refreshResponse = authApi.refresh("Bearer $refreshToken")
                        val newAccessToken = refreshResponse.data.accessToken
                        val name = tokenDataStore.getUserName().first()

                        tokenDataStore.saveTokens(
                            accessToken = newAccessToken,
                            refreshToken = refreshToken,
                            name = name
                        )

                        // 3. 갱신된 토큰으로 재시도
                        val retryLogout = authRepository.logout(newAccessToken)
                        if (retryLogout) {
                            tokenDataStore.clearTokens()
                            _isLoggedIn.value = false
                            onSuccess()
                        } else {
                            Timber.tag("auth").e("🚫 토큰 갱신 후 로그아웃도 실패")
                            onFailure()
                        }
                    } catch (e: Exception) {
                        Timber.tag("auth").e("🚫 Refresh 실패: ${e.message}")
                        tokenDataStore.clearTokens()
                        _isLoggedIn.value = false
                        onFailure()
                    }
                }

            } catch (e: Exception) {
                Timber.tag("auth").e("🚫 Logout 예외 발생: ${e.message}")
                onFailure()
            }
        }
    }


    // 회원탈퇴
    fun signOut() {
        _isSignup.value = false
    }

//    fun saveJwt(accessToken: String, refreshToken: String){
//        viewModelScope.launch {
//            jwtUseCase.saveTokens(accessToken = accessToken, refreshToken = refreshToken)
//        }
//
//    }

    // 리프레시 토큰
    fun refreshToken(
        onSuccess: () -> Unit = {}, onFailure: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val refreshToken = tokenDataStore.getRefreshToken().first()
                val response = authApi.refresh("Bearer $refreshToken")

                // 서버에서 새로 받아온 토큰
                val newAccessToken = response.data.accessToken

                val name = tokenDataStore.getUserName().first()

//                saveJwt(newAccessToken,refreshToken)

                // 새 토큰 저장
                tokenDataStore.saveTokens(
                    accessToken = newAccessToken, refreshToken = refreshToken, name = name
                )

                _isLoggedIn.value = true
                onSuccess()
            } catch (e: Exception) {
                _isLoggedIn.value = false
                onFailure(e)
            }
        }
    }

}