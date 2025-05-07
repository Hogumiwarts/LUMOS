package com.hogumiwarts.lumos.ui.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
) : ViewModel() {
    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.inputId -> _state.update {
                it.copy(
                    id = intent.id,
                    idErrorMessage = null
                )
            }

            is LoginIntent.inputPw -> _state.update {
                it.copy(
                    pw = intent.pw,
                    pwErrorMessage = null
                )
            }

            is LoginIntent.togglePasswordVisibility -> _state.update { it.copy(passwordVisible = !it.passwordVisible) }
            is LoginIntent.submitLogin -> validateAndLogin()
        }
    }

    private fun validateAndLogin() {
        val id = _state.value.id
        val pw = _state.value.pw

        when {
            !id.contains("@") -> _state.update { it.copy(idErrorMessage = "아이디는 이메일 주소 형태로 입력해 주세요.") }
            id != "ssafy@ssafy.com" -> _state.update { it.copy(idErrorMessage = "등록되지 않은 아이디입니다.") }
            pw != "1234" -> _state.update { it.copy(pwErrorMessage = "비밀번호가 일치하지 않습니다.") }
            else -> {
                viewModelScope.launch {
                    _effect.send(LoginEffect.ShowWelcomeToast)
                    _effect.send(LoginEffect.NavigateToHome)
                }
            }
        }
    }

}