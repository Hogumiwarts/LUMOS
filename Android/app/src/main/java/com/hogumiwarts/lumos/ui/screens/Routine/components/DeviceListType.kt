package com.hogumiwarts.lumos.ui.screens.Routine.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hogumiwarts.lumos.R

// 기기 타입별 아이콘 & 색상 매핑
enum class DeviceListType(
    val deviceName: String,
    val iconResId: Int,
    val color: Color,
    val iconTopPadding: Dp
) {
    AIR_CLEANER("공기청정기", R.drawable.ic_device_list_air_cleaner, Color(0xFF334093), iconTopPadding = 0.dp ),
    LIGHT("조명", R.drawable.ic_light_off, Color(0xFF717BBC), iconTopPadding = 0.dp),
    SPEAKER("스피커", R.drawable.ic_device_list_speaker, Color(0xFF4B5BA9), iconTopPadding = (-10).dp),
    SWITCH("스위치", R.drawable.ic_device_list_switch, Color(0xFFA9AFD9), iconTopPadding = 0.dp);

    companion object {
        fun from(type: String): DeviceListType =
            values().find { it.deviceName == type } ?: LIGHT
    }
}