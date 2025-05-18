package com.hogumiwarts.lumos.presentation.ui.viewmodel
sealed class TokenState {
    object Loading : TokenState()
    data class Loaded(val token: String) : TokenState()
}