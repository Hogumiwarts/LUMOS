package com.hogumiwarts.lumos.ui.screens.routine.components

import androidx.compose.ui.graphics.Color
import com.hogumiwarts.lumos.R

// 기기 타입별 아이콘 & 색상 매핑
enum class DeviceListType(
    val categoryName: String,
    val iconResId: Int,
    val color: Color,
) {
    AIR_CLEANER("AirPurifier", R.drawable.ic_device_list_air_cleaner, Color(0xFF334093)),
    LIGHT("Light", R.drawable.ic_light_off, Color(0xFF717BBC)),
    SPEAKER("Speaker", R.drawable.ic_device_list_speaker, Color(0xFF4B5BA9)),
    SWITCH("Switch", R.drawable.ic_device_list_switch, Color(0xFFA9AFD9)),
    ETC("Etc", R.drawable.ic_light_off, Color.Gray); // 기본값

    companion object {
        fun from(category: String): DeviceListType =
            values().find { it.categoryName.equals(category, ignoreCase = true) } ?: ETC
    }
}