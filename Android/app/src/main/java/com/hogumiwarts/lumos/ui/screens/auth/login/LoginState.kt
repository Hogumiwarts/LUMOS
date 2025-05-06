package com.hogumiwarts.lumos.ui.screens.auth.login

data class LoginState(
    val id: String = "",
    val pw: String = "",
    val passwordVisible: Boolean = false,
    val idErrorMessage: String? = null,
    val pwErrorMessage: String? = null,
    val isLoggedIn: Boolean = false
)
