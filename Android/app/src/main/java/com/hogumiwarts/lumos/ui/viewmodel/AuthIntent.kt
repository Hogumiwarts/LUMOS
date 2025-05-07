package com.hogumiwarts.lumos.ui.viewmodel

sealed class AuthIntent {
    object SignUp : AuthIntent()
}