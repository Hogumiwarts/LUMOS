package com.hogumiwarts.lumos.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hogumiwarts.lumos.ui.screens.Home.HomeScreen
import com.hogumiwarts.lumos.ui.screens.Setting.SettingScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(
            route = BottomNavItem.Home.route,
            // 홈 화면으로 들어올 때는 왼쪽에서 오른쪽으로 슬라이드
            enterTransition = {
                when (initialState.destination.route) {
                    BottomNavItem.Settings.route ->
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    else -> null
                }
            },
            // 홈 화면에서 나갈 때는 왼쪽에서 오른쪽으로 슬라이드
            exitTransition = {
                when (targetState.destination.route) {
                    BottomNavItem.Settings.route ->
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    else -> null
                }
            }
        ) {
            HomeScreen()
        }

        composable(
            route = BottomNavItem.Settings.route,
            // 설정 화면으로 들어올 때는 오른쪽에서 왼쪽으로 슬라이드
            enterTransition = {
                when (initialState.destination.route) {
                    BottomNavItem.Home.route ->
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(300)
                        )
                    else -> null
                }
            },
            // 설정 화면에서 나갈 때는 오른쪽에서 왼쪽으로 슬라이드
            exitTransition = {
                when (targetState.destination.route) {
                    BottomNavItem.Home.route ->
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    else -> null
                }
            }
        ) {
            SettingScreen()
        }
    }
}