package com.hogumiwarts.lumos.ui.screens.auth.signup

data class SignupState(
    val id: String = "",
    val pw: String = "",
    val name: String = "",
    val passwordVisible: Boolean = false,
    val idErrorMessage: String? = null,
    val pwErrorMessage: String? = null,
    val nameErrorMessage: String? = null,
    val isSignup: Boolean = false
)