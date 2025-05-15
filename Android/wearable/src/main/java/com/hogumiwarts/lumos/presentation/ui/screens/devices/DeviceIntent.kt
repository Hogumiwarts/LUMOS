package com.hogumiwarts.lumos.presentation.ui.screens.devices


sealed class DeviceIntent {
    object LoadDevice: DeviceIntent()
    object Refresh : DeviceIntent() // 새로고침
}