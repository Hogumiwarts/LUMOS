package com.hogumiwarts.lumos.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.hogumiwarts.lumos.R

sealed class BottomNavItem(
    val route: String,
    val title: String,
    @DrawableRes val icon: Int
) {
    object Home : BottomNavItem(
        route = "home",
        title = "홈",
        icon = R.drawable.ic_home_off
    )

    object Settings : BottomNavItem(
        route = "settings",
        title = "설정",
        icon = R.drawable.ic_menu_off
    )

    object Info : BottomNavItem(
        route = "info",
        title = "기기 목록",
        icon = R.drawable.ic_device_off
    )

    object Routine : BottomNavItem(
        route = "routine",
        title = "나의 루틴",
        icon = R.drawable.ic_routine_off
    )
}