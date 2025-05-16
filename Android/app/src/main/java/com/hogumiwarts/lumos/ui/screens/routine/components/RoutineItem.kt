package com.hogumiwarts.lumos.ui.screens.routine.components

import com.hogumiwarts.domain.model.CommandDevice
import com.hogumiwarts.lumos.R

data class RoutineItem(
    val routineName: String,
    val routineIcon: String,
    val devices: List<CommandDevice>,
    val gestureId: Int,
    val gestureName: String,
    val gestureImageUrl: String,
    val gestureDescription: String
) {
//    companion object {
//        val sample = listOf(
//            RoutineItem(1, "수면 루틴", "핑거스냅", R.drawable.ic_moon_sleep),
//            RoutineItem(2, "아침 루틴", "손목 스윙", R.drawable.ic_sun),
//            RoutineItem(3, "즐거운 코딩 시간", "박수", R.drawable.ic_laptop)
//        )
//    }
}
