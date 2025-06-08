package com.hogumiwarts.lumos.ui.screens.home.components

import com.hogumiwarts.lumos.R

enum class HomeDeviceType(
    val koreanName: String,
    val imageResId: Int
) {
    AUDIO("스피커", R.drawable.img_device_speaker),
    AIRPURIFIER("공기청정기", R.drawable.img_device_air),
    SWITCH("스위치", R.drawable.img_device_switch);

    companion object {
        fun from(type: String): HomeDeviceType? = entries.find { it.name == type }
    }
}