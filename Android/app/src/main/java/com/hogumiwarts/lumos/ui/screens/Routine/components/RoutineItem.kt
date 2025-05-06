package com.hogumiwarts.lumos.ui.screens.Routine.components

import com.hogumiwarts.lumos.R

data class RoutineItem(
    val title: String,
    val subtitle: String,
    val iconResId: Int
) {
    companion object {
        val sample = listOf(
            RoutineItem("수면 루틴", "핑거스냅", R.drawable.ic_moon_sleep),
            RoutineItem("아침 루틴", "손목 스윙", R.drawable.ic_sun),
            RoutineItem("즐거운 코딩 시간", "박수", R.drawable.ic_laptop)
        )
    }
}
