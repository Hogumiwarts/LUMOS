package com.hogumiwarts.lumos.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Info,
        null,
        BottomNavItem.Routine,
        BottomNavItem.Settings,
    )

    // 화면 순서 정의 (왼쪽에서 오른쪽 순서로)
    val routes = items.map { it?.route }

    NavigationBar(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            if (item == null) {
                Spacer(Modifier.weight(1f, fill = true))
            } else {
                NavigationBarItem(
                    icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                    label = { Text(text = item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            // 방향성을 고려한 네비게이션 사용
                            navController.navigateWithSlideDirection(item.route, routes)
                        }
                    }
                )
            }
        }
    }
}