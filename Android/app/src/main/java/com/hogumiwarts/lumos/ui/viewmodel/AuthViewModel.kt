package com.hogumiwarts.lumos.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.datastore.TokenDataStore
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
    private val tokenDataStore: TokenDataStore
) : ViewModel() {

    val _isLogginIn = MutableStateFlow<Boolean?>(true)
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

    // 회원가입
    fun signUp(){
        _isSignup.value = true
    }

    // 회원탈퇴
    fun signOut(){
        _isSignup.value = false
    }
}