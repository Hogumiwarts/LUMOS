package com.hogumiwarts.lumos.ui.screens.auth.login

sealed class LoginEffect {
    object ShowWelcomeToast : LoginEffect()
    object NavigateToHome : LoginEffect()
}
