package com.hogumiwarts.lumos.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hogumiwarts.lumos.ui.screens.Control.ControlScreen
import com.hogumiwarts.lumos.ui.screens.Home.HomeScreen
import com.hogumiwarts.lumos.ui.screens.Setting.SettingScreen
import com.hogumiwarts.lumos.ui.screens.Devices.InfoScreen
import com.hogumiwarts.lumos.ui.screens.Routine.RoutineScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // 화면 순서 정의 (바텀 네비게이션 순서와 일치)
    val screens = listOf(
        BottomNavItem.Home,
        BottomNavItem.Info,
        BottomNavItem.Routine,
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
                            animationSpec = tween(300)
                        )
                    } else {
                        // 왼쪽에서 들어옴
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(300)
                        )
                    }
                },
                exitTransition = {
                    val toRoute = targetState.destination.route

                    // ControlScreen으로 이동할 때는 현재 화면 애니메이션 없음
                    if (toRoute == "controlScreen") {
                        ExitTransition.None
                    } else {
                        val fromRoute = initialState.destination.route

                        if (fromRoute != null && toRoute != null && getNavigationDirection(fromRoute, toRoute, screenOrder)) {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        } else {
                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            )
                        }
                    }
                },
                // 뒤로가기로 진입 시 애니메이션 (ControlScreen에서 돌아올 때)
                popEnterTransition = {
                    val fromRoute = initialState.destination.route

                    // ControlScreen에서 돌아올 때 애니메이션 없음
                    if (fromRoute == "controlScreen") {
                        EnterTransition.None
                    } else {
                        val toRoute = targetState.destination.route

                        if (fromRoute != null && toRoute != null && getNavigationDirection(fromRoute, toRoute, screenOrder)) {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        } else {
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            )
                        }
                    }
                }
            ) {
                // 각 화면에 맞는 Composable 함수 호출
                when (item) {
                    BottomNavItem.Home -> HomeScreen()

                    BottomNavItem.Info -> InfoScreen()

                    BottomNavItem.Routine -> RoutineScreen()

                    BottomNavItem.Settings -> SettingScreen()
                }
            }
        }

        composable(
            route = "controlScreen",
            enterTransition = {
                // 아래에서 위로 올라오는 애니메이션
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(200)
                )
            },
            exitTransition = {
                // 위에서 아래로 내려가는 애니메이션
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(200)
                )
            }
        ) {
            ControlScreen()
        }


    }
}