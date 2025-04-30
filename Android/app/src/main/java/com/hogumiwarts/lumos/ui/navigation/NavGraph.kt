package com.hogumiwarts.lumos.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hogumiwarts.lumos.ui.screens.Home.HomeScreen
import com.hogumiwarts.lumos.ui.screens.Setting.SettingScreen
import com.hogumiwarts.lumos.ui.screens.Info.InfoScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // 화면 순서 정의 (바텀 네비게이션 순서와 일치)
    val screens = listOf(
        BottomNavItem.Info,
        BottomNavItem.Home,
        BottomNavItem.Settings,
    )

    // 화면 경로만 추출
    val screenOrder = screens.map { it.route }

    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        // 각 화면에 대한 composable 설정
        screens.forEach { item ->
            composable(
                route = item.route,
                enterTransition = {
                    val fromRoute = initialState.destination.route
                    val toRoute = targetState.destination.route

                    // 현재 화면이 왼쪽, 새 화면이 오른쪽일 때 (왼→오 이동)
                    if (fromRoute != null && toRoute != null && getNavigationDirection(fromRoute, toRoute, screenOrder)) {
                        // 오른쪽에서 들어옴
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(200)
                        )
                    } else {
                        // 왼쪽에서 들어옴
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(200)
                        )
                    }
                },
                exitTransition = {
                    val fromRoute = initialState.destination.route
                    val toRoute = targetState.destination.route

                    // 현재 화면이 왼쪽, 새 화면이 오른쪽일 때 (왼→오 이동)
                    if (fromRoute != null && toRoute != null && getNavigationDirection(fromRoute, toRoute, screenOrder)) {
                        // 왼쪽으로 나감
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(200)
                        )
                    } else {
                        // 오른쪽으로 나감
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(200)
                        )
                    }
                }
            ) {
                // 각 화면에 맞는 Composable 함수 호출
                when (item) {
                    BottomNavItem.Home -> HomeScreen()
                    BottomNavItem.Settings -> SettingScreen()
                    BottomNavItem.Info -> InfoScreen()
                }
            }
        }
    }
}