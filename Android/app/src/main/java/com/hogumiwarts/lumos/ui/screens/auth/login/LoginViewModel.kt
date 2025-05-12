package com.hogumiwarts.lumos.ui.screens.auth.login

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.LoginResult
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.lumos.datastore.TokenDataStore
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore,
    @ApplicationContext private val context: Context
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
            is LoginIntent.submitLogin -> validateAndLogin(context)


        }
    }

    private fun validateAndLogin(context: Context) {
        val id = _state.value.id
        val pw = _state.value.pw

        // 1차 유효성 검사
        when {
            !isEmailFormatValid(id) -> {
                _state.update { it.copy(idErrorMessage = "아이디는 이메일 주소 형태로 입력해 주세요.") }
                return
            }

            pw.isBlank() -> {
                _state.update { it.copy(pwErrorMessage = "비밀번호를 입력해 주세요.") }
                return
            }

            id.isBlank() -> {
                _state.update { it.copy(pwErrorMessage = "아이디를 입력해 주세요.") }
                return
            }
        }

        // api 호출
        viewModelScope.launch {
            when (val result = authRepository.login(id, pw)) {
                is LoginResult.Success -> {
                    // log로 정보 확인
                    Timber.tag("Login").i(
                        """
                        ✅ 로그인 성공!!
                        ├─ ID       : ${result.memberId}
                        ├─ Email    : ${result.email}
                        ├─ Name     : ${result.name}
                        ├─ Access   : ${result.accessToken}
                        └─ Refresh  : ${result.refreshToken}
                        """.trimIndent()
                    )

                    _effect.send(LoginEffect.ShowWelcomeToast)
                    _effect.send(LoginEffect.NavigateToHome)

                    // 토큰 저장
                    tokenDataStore.saveTokens(
                        accessToken = result.accessToken,
                        refreshToken = result.refreshToken
                    )
                }

                // 에러 처리
                LoginResult.UserNotFound -> {
                        _state.update { it.copy(idErrorMessage = "존재하지 않는 사용자입니다.") }
                }
                LoginResult.InvalidPassword -> {
                    _state.update { it.copy(pwErrorMessage = "비밀번호가 틀렸습니다.") }
                }
                LoginResult.NetworkError -> {
                    _state.update { it.copy(pwErrorMessage = "네트워크 오류가 발생했습니다.") }
                }
                LoginResult.UnknownError -> {
                    _state.update { it.copy(pwErrorMessage = "알 수 없는 오류가 발생했습니다.") }
                }
            }
        }
    }

}

private fun isEmailFormatValid(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return emailRegex.matches(email)
}