package com.hogumiwarts.lumos.ui.screens.auth.signup

import android.content.Context

sealed class SignupIntent {
    data class inputId(val id: String) : SignupIntent()
    data class inputPw(val pw: String) : SignupIntent()
    data class inputName(val name: String) : SignupIntent()
    object togglePasswordVisibility : SignupIntent()
    data class submitSignup(val context: Context) : SignupIntent()
}