package com.hogumiwarts.lumos.ui.screens.auth.signup

data class SignupState(
    val id: String = "",
    val pw: String = "",
    val pw2: String = "",
    val name: String = "",
    val passwordVisible: Boolean = false,
    val password2Visible: Boolean = false,
    val idErrorMessage: String? = null,
    val pwErrorMessage: String? = null,
    val pw2ErrorMessage: String? = null,
    val nameErrorMessage: String? = null,
    val isSignup: Boolean = false
)