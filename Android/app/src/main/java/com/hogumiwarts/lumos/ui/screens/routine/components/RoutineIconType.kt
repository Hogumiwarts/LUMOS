package com.hogumiwarts.lumos.ui.screens.routine.components

import androidx.annotation.DrawableRes
import com.hogumiwarts.lumos.R

enum class RoutineIconType(val iconName: String, @DrawableRes val iconResId: Int) {
    MOON_SLEEP("sleep", R.drawable.ic_moon_sleep),
    ALARM_CLOCK("alarm", R.drawable.ic_alarm_clock),
    COFFEE("coffee", R.drawable.ic_coffee),
    SUN("sun", R.drawable.ic_sun),
    LAPTOP("laptop", R.drawable.ic_laptop),
    BED("bed", R.drawable.ic_bed);

    companion object {
        fun fromName(name: String): RoutineIconType? {
            return values().find { it.iconName.equals(name.trim(), ignoreCase = true) }
        }


        fun getResIdByName(name: String?): Int {
            return fromName(name?.trim().orEmpty())?.iconResId ?: R.drawable.ic_laptop
        }
    }

}

