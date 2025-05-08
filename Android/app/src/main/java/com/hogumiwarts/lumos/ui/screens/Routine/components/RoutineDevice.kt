package com.hogumiwarts.lumos.ui.screens.Routine.components

import com.hogumiwarts.lumos.R

data class RoutineDevice(
    val deviceName: String,
    val deviceType: String,
    val isOn: Boolean,
    val iconResId: Int
) {
    companion object {
        val sample = listOf(
            RoutineDevice("거실 공기청정기", "공기청정기", true, R.drawable.ic_device_aircleaner),
            RoutineDevice("내 방 조명 1", "조명", false, R.drawable.ic_device_light)
        )
    }
}