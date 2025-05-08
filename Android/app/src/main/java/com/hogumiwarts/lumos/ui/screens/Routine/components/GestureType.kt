package com.hogumiwarts.lumos.ui.screens.Routine.components

import com.hogumiwarts.lumos.R

enum class GestureType(val gestureName: String, val gestureiconResId: Int) {
    FIST_ROTATE_180("주먹 회전", R.drawable.ic_gesture_fist_rotate_180), // 주먹쥠 상태에서 180도 회전
    DOUBLE_CLAP("박수 두 번", R.drawable.ic_gesture_clap), // 박수 2번
    OPEN_HAND_MOVE_LEFT("손 펼쳐 왼쪽 이동", R.drawable.ic_gesture_move_left), // 손 편 상태로 왼쪽으로 이동
    OPEN_HAND_MOVE_RIGHT("손 펼쳐 오른쪽 이동", R.drawable.ic_gesture_move_right) // 손 편 상태로 오른쪽으로 이동
}