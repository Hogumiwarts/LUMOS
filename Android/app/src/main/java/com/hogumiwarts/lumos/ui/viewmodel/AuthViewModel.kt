package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.data.source.remote.AuthApi
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

            if (token.isNotEmpty()) {
                // 서버 요청 전에 accessToken이 만료되었을 수 있으므로 refresh 시도
//                refreshToken(
//                    onSuccess = {
//                        _isLogginIn.value = true
//                        Timber.tag("Auth").d("✅ 토큰 갱신 완료: $token")
//                    },
//                    onFailure = {
//                        _isLogginIn.value = false
//                    }
//                )

            } else {
                _isLoggedIn.value = false
            }
        }
    }

    // 로그인
    fun logIn() {
        _isLoggedIn.value = true
    }

    // 로그아웃
    fun logOut() {
        _isLoggedIn.value = false
        viewModelScope.launch { tokenDataStore.clearTokens() }
    }

    // 회원탈퇴
    fun signOut() {
        _isSignup.value = false
    }

    // 리프레시 토큰
    fun refreshToken(
        onSuccess: () -> Unit = {},
        onFailure: (Throwable) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val refreshToken = tokenDataStore.getRefreshToken().first()
                val response = authApi.refresh("Bearer $refreshToken")

                // 서버에서 새로 받아온 토큰
                val newAccessToken = response.data.accessToken

                val name = tokenDataStore.getUserName().first()

                // 새 토큰 저장
                tokenDataStore.saveTokens(
                    accessToken = newAccessToken,
                    refreshToken = refreshToken,
                    name = name
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