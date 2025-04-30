package com.hogumiwarts.lumos.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigation(
    navController: NavController
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
        modifier = Modifier
            .fillMaxWidth(),
        containerColor = Color.White,
        tonalElevation = 10.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            if (item == null) {
                Spacer(Modifier.weight(1f, fill = true))
            } else {
                NavigationBarItem(
                    icon = {
                        Image(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = { Text(text = item.title) },
                    selected = currentRoute == item.route,
                    onClick = {
                        if (currentRoute != item.route) {
                            // 방향성을 고려한 네비게이션 사용
                            navController.navigateWithSlideDirection(item.route, routes)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF3F51B5),    // 선택된 아이콘 색상
                        selectedTextColor = Color(0xFF3F51B5),    // 선택된 텍스트 색상
                        indicatorColor = Color.Transparent,       // 선택 표시자를 투명하게 설정하여 제거
                        unselectedIconColor = Color.Gray,         // 선택되지 않은 아이콘 색상
                        unselectedTextColor = Color.Gray          // 선택되지 않은 텍스트 색상
                    )
                )
            }
        }
    }
}