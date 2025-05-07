package com.hogumiwarts.lumos.ui.screens.auth.signup

sealed class SignupEffect {
    object NavigateToLogin : SignupEffect()
    object ShowSignupSuccessToast : SignupEffect()
    object SignupCompleted : SignupEffect()
}