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
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggin: StateFlow<Boolean> = _isLoggedIn

    init{
        viewModelScope.launch {
            val token = TokenDataStore.getAccessToken(context).first()
            _isLoggedIn.value = token.isNotEmpty()
        }
    }

    fun logIn() {
        _isLoggedIn.value = true
    }

    fun logOut() {
        _isLoggedIn.value = false
    }
}