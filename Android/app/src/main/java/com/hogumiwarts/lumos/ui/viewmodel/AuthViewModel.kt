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
import javax.inject.Inject

// 전체 로그인 여부 관리
@HiltViewModel
class AuthViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val tokenDataStore: TokenDataStore,
    private val authApi: AuthApi
) : ViewModel() {

    val _isLogginIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLogginIn

    val _isSignup = MutableStateFlow<Boolean?>(null)
    val isSignup: StateFlow<Boolean?> = _isSignup

    init {
        viewModelScope.launch {
            val token = tokenDataStore.getAccessToken().first()
            _isLogginIn.value = token.isNotEmpty()
        }
    }

    // 로그인
    fun logIn() {
        _isLogginIn.value = true
    }

    // 로그아웃
    fun logOut() {
        _isLogginIn.value = false
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

                val name = tokenDataStore.getUserName()

                // 새 토큰 저장
                tokenDataStore.saveTokens(
                    accessToken = newAccessToken,
                    refreshToken = refreshToken,
                    name = name.toString()
                )

                _isLogginIn.value = true
                onSuccess()
            } catch (e: Exception) {
                _isLogginIn.value = false
                onFailure(e)
            }
        }
    }

}