package com.hogumiwarts.lumos.ui.screens.Routine.components

import androidx.compose.ui.graphics.Color
import com.hogumiwarts.lumos.R

// 기기 타입별 아이콘 & 색상 매핑
enum class DeviceListType (
    val deviceName: String,
    val iconResId: Int,
    val color: Color,
){
    AIR_CLEANER("공기청정기", R.drawable.ic_device_list_air_cleaner, Color(0xFF334093)),
    LIGHT("조명", R.drawable.ic_light_off, Color(0xFF717BBC)),
    SPEAKER("스피커", R.drawable.ic_device_list_speaker, Color(0xFF4B5BA9)),
    SWITCH("스위치", R.drawable.ic_device_list_switch, Color(0xFFA9AFD9));

    companion object{
        fun from(type: String): DeviceListType =
            values().find { it.deviceName == type } ?: LIGHT
    }
}