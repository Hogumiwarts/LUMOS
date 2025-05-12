package com.hogumiwarts.lumos.ui.screens.routine.components

import com.hogumiwarts.lumos.R

// todo: 실제 api 연동 필요
data class RoutineItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconResId: Int
) {
    companion object {
        val sample = listOf(
            RoutineItem(1, "수면 루틴", "핑거스냅", R.drawable.ic_moon_sleep),
            RoutineItem(2, "아침 루틴", "손목 스윙", R.drawable.ic_sun),
            RoutineItem(3, "즐거운 코딩 시간", "박수", R.drawable.ic_laptop)
        )
    }
}
