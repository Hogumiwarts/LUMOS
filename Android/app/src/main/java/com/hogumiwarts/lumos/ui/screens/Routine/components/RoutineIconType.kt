package com.hogumiwarts.lumos.ui.screens.Routine.components

import androidx.annotation.DrawableRes
import com.hogumiwarts.lumos.R

// todo: iconName은 api 연결 시 이름이나 id 등과 연동하기
// icon이 어떻게 저장되는지 아직 잘 몰라서 일단 임시로 만들어놓음

enum class RoutineIconType(val iconName: String, @DrawableRes val iconResId: Int) {
    MOON_SLEEP("취침", R.drawable.ic_moon_sleep),
    ALARM_CLOCK("알람", R.drawable.ic_alarm_clock),
    COFFEE("커피", R.drawable.ic_coffee),
    SUN("햇살", R.drawable.ic_sun),
    BED("침대", R.drawable.ic_bed)
}