package com.hogumiwarts.lumos.ui.screens.auth.login

sealed class LoginIntent {
    data class inputId(val id: String) : LoginIntent()
    data class inputPw(val pw: String) : LoginIntent()
    object togglePasswordVisibility : LoginIntent()
    object submitLogin : LoginIntent()
}
