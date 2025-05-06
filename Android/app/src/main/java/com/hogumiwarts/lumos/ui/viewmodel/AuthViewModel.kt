package com.hogumiwarts.lumos.ui.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {

    // 임시 로그인 상태
    // todo: 실제 로그인 구현 시 변경할 것!
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggin: StateFlow<Boolean> = _isLoggedIn

    // 임시로 로그인 상태 바꾸는 함수 만들어 둠
    fun logIn(){
        _isLoggedIn.value = true
    }

    fun logOut(){
        _isLoggedIn.value = false
    }
}