package com.hogumiwarts.lumos.ui.screens.Routine.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.hogumiwarts.lumos.R

// 기기 타입별 아이콘 & 색상 매핑
enum class DeviceType(
    val deviceName: String,
    val iconResId: Int,
    val color: Color,
) {
    AIR_CLEANER("공기청정기", R.drawable.ic_device_aircleaner, Color(0xFF334093)),
    LIGHT("조명", R.drawable.ic_device_light, Color(0xFF717BBC)),
    SPEAKER("스피커", R.drawable.ic_device_speaker, Color(0xFF4B5BA9)),
    SWITCH("스위치", R.drawable.ic_device_switch, Color(0xFFA9AFD9));

    companion object {
        fun from(type: String): DeviceType =
            entries.find { it.deviceName == type } ?: LIGHT
    }
}