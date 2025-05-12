package com.hogumiwarts.lumos.ui.screens.auth.signup

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hogumiwarts.domain.model.SignupResult
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.lumos.datastore.TokenDataStore
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _state = MutableStateFlow(SignupState())
    val state: StateFlow<SignupState> = _state

    private val _effect = MutableSharedFlow<SignupEffect>()
    val effect = _effect

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

            is SignupIntent.inputPw2 -> _state.update {
                it.copy(
                    pw2 = intent.pw2,
                    pw2ErrorMessage = null
                )
            }

            is SignupIntent.submitSignup -> validateAndSignup(context)
            is SignupIntent.togglePasswordVisibility -> _state.update { it.copy(passwordVisible = !it.passwordVisible) }
            is SignupIntent.togglePassword2Visibility -> _state.update { it.copy(password2Visible = !it.password2Visible) }

        }
    }

    private fun validateAndSignup(context: Context) {
        val id = _state.value.id
        val pw = _state.value.pw
        val name = _state.value.name
        val pw2 = _state.value.pw2

        // 1차 유효성 검사
        when {
            !isEmailFormatValid(id) -> {
                _state.update { it.copy(idErrorMessage = "아이디는 이메일 주소 형태로 입력해 주세요.") }
                return
            }

            id.isBlank() -> {
                _state.update { it.copy(idErrorMessage = "아이디를 입력해 주세요.") }
                return
            }

            pw.isBlank() -> {
                _state.update { it.copy(pwErrorMessage = "비밀번호를 입력해 주세요.") }
                return
            }

            pw2.isBlank() -> {
                _state.update { it.copy(pw2ErrorMessage = "비밀번호를 입력해 주세요.") }
                return
            }

            pw != pw2 -> {
                _state.update { it.copy(pw2ErrorMessage = "비밀번호가 서로 일치하지 않습니다.") }
                return
            }

            name.isBlank() -> {
                _state.update { it.copy(nameErrorMessage = "이름을 입력해 주세요.") }
                return
            }

        }

        // api 호출
        viewModelScope.launch {
            try{
                Log.d("SignupViewModel", "회원가입 시도 중...")
                val result = authRepository.signup(id, pw, pw2, name)
                Log.d("SignupViewModel", "결과: $result")

                when (result) {

                    is SignupResult.Success -> {
                        Log.d("Signup", "✅ 회원가입 성공!")

                        // 가입 성공 시 이펙트 전송
//                        _effect.emit(SignupEffect.SignupCompleted)
                        delay(100)
                        _effect.emit(SignupEffect.ShowSignupSuccessToast)
                        delay(100)
                        _effect.emit(SignupEffect.NavigateToLogin)
                    }

                    SignupResult.PasswordMismatch -> {
                        _state.update { it.copy(pwErrorMessage = "비밀번호가 서로 일치하지 않습니다.") }
                    }

                    SignupResult.EmailAlreadyExists -> {
                        _state.update { it.copy(idErrorMessage = "이미 존재하는 ID입니다.") }
                    }

                    SignupResult.NetworkError -> {
                        _state.update { it.copy(pwErrorMessage = "네트워크 오류가 발생했습니다.") }
                    }

                    SignupResult.UnknownError -> {
                        _state.update { it.copy(pwErrorMessage = "알 수 없는 오류가 발생했습니다.") }
                    }
                }

            } catch (e: Exception){
                _state.update{it.copy(pwErrorMessage = "예기치 못한 에러가 발생했습니다.")}
                Log.e("Signup", "SignupViewModel: 회원 가입 실패", e)
            }

        }
    }
}

private fun isEmailFormatValid(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
    return emailRegex.matches(email)
}

