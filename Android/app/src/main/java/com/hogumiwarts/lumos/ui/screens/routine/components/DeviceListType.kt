package com.hogumiwarts.lumos.ui.screens.routine.components

import androidx.compose.ui.graphics.Color
import com.hogumiwarts.lumos.R

// 기기 타입별 아이콘 & 색상 매핑
enum class DeviceListType(
    val categoryName: String,
    val iconResId: Int,
    val color: Color,
    val deviceName: String
) {
    AIRPURIFIER("공기청정기", R.drawable.ic_device_list_air_cleaner, Color(0xFF334093), "공기청정기1"),
    LIGHT("조명", R.drawable.ic_light_off, Color(0xFF717BBC), "내 방 조명"),
    AUDIO("스피커", R.drawable.ic_device_list_speaker, Color(0xFF4B5BA9), "스피커12"),
    SWITCH("스위치", R.drawable.ic_device_list_switch, Color(0xFFA9AFD9), "거실 스위치"),
    ETC("Etc", R.drawable.ic_light_off, Color.Gray, "기타"); // 기본값

    companion object {
        fun from(category: String): DeviceListType =
            values().find { it.name.equals(category.trim(), ignoreCase = true) } ?: ETC
    }
}