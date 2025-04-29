package com.hogumiwarts.lumos.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = "home",
        title = "홈",
        icon = Icons.Default.Home
    )

    object Settings : BottomNavItem(
        route = "settings",
        title = "설정",
        icon = Icons.Default.Settings
    )

    object Info : BottomNavItem(
        route = "info",
        title = "정보",
        icon = Icons.Default.Info
    )
}