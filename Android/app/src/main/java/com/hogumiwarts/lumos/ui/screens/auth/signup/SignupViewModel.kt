package com.hogumiwarts.lumos.ui.screens.auth.signup

import android.content.Context
import androidx.lifecycle.ViewModel
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.lumos.datastore.TokenDataStore
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenDataStore: TokenDataStore,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state

    private val _effect = Channel<SignupEffect>()
    val effect = _effect.receiveAsFlow()

    fun handleIntent(intent: SignupIntent) {
        when (intent) {
            is SignupIntent.inputId -> _state.update {
                it.copy(
                    id = intent.id,
                    idErrorMessage = null
                )
            }

            is SignupIntent.inputName -> _state.update {
                it.copy(
                    name = intent.name,
                    nameErrorMessage = null
                )
            }

            is SignupIntent.inputPw -> _state.update {
                it.copy(
                    pw = intent.pw,
                    pwErrorMessage = null
                )
            }

            is SignupIntent.submitSignup -> validateAndSignup(context)
            is SignupIntent.togglePasswordVisibility -> _state.update { it.copy(passwordVisible = !it.passwordVisible) }
        }
    }

    private fun validateAndSignup(context: Context) {
        val id = _state.value.id
        val pw = _state.value.pw
        val name = _state.value.name

        // 1차 유효성 검사
        when {
            id.contains("@") -> {
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

            name.isBlank() -> {
                _state.update { it.copy(pwErrorMessage = "이름을 입력해 주세요.") }
                return
            }
        }

        //todo: api 호출
    }
}