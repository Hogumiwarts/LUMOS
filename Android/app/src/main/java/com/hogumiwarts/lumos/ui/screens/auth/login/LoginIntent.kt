package com.hogumiwarts.lumos.ui.screens.auth.login

import android.content.Context

sealed class LoginIntent {
    data class inputId(val id: String) : LoginIntent()
    data class inputPw(val pw: String) : LoginIntent()
    object togglePasswordVisibility : LoginIntent()
    data class submitLogin(val context: Context) : LoginIntent()
}
