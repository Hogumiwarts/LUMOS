package com.hogumiwarts.lumos.presentation.ui.screens.gesture

sealed class GestureIntent {
    data class LoadGestureDetail(val deviceId: Long): GestureIntent()
}