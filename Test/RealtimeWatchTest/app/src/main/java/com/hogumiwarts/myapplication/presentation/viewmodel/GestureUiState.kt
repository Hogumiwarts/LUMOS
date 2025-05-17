package com.hogumiwarts.myapplication.presentation.viewmodel

data class GestureUiState(
    val isListening: Boolean = false,
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val showActivationIndicator: Boolean = false,
    val activationProgress: Float = 0f,
    val errorMessage: String? = null
)