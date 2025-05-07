package com.hogumiwarts.lumos.ui.navigation

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hogumiwarts.lumos.MainScreen
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel
import com.hogumiwarts.lumos.ui.screens.Control.ControlScreen
import com.hogumiwarts.lumos.ui.screens.Home.HomeScreen
import com.hogumiwarts.lumos.ui.screens.Setting.SettingScreen
import com.hogumiwarts.lumos.ui.screens.Devices.InfoScreen
import com.hogumiwarts.lumos.ui.screens.Routine.RoutineScreen
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginScreen
import com.hogumiwarts.lumos.ui.screens.auth.onboarding.WelcomeScreen

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

    val viewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by viewModel.isLoggin.collectAsState()

    val startDestination = if (isLoggedIn) "home" else "welcome"

    NavHost(
        navController = navController,
        //startDestination = BottomNavItem.Home.route,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // 시작 화면
        composable("welcome") {
            WelcomeScreen(
                onStartClick = { navController.navigate("signup") },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // 각 화면에 대한 composable 설정
        screens.forEach { item ->
            composable(
                route = item.route,
                enterTransition = {
                    val fromRoute = initialState.destination.route
                    val toRoute = targetState.destination.route

                    // 현재 화면이 왼쪽, 새 화면이 오른쪽일 때 (왼→오 이동)
                    if (fromRoute != null && toRoute != null && getNavigationDirection(
                            fromRoute,
                            toRoute,
                            screenOrder
                        )
                    ) {
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

                    // ControlScreen으로 이동할 때
                    // 현재 화면이 위로 올라가는 애니메이션
                    if (toRoute == "controlScreen") {

                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(200)
                        )

                    } else {
                        val fromRoute = initialState.destination.route

                        if (fromRoute != null && toRoute != null && getNavigationDirection(
                                fromRoute,
                                toRoute,
                                screenOrder
                            )
                        ) {
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

                    // ControlScreen에서 돌아올 때
                    if (fromRoute == "controlScreen") {

                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(200)
                        )

                    } else {
                        val toRoute = targetState.destination.route

                        if (fromRoute != null && toRoute != null && getNavigationDirection(
                                fromRoute,
                                toRoute,
                                screenOrder
                            )
                        ) {
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
            // ControlScreen 진입 시 - 아래에서 위로 올라옴
            enterTransition = {
                // 아래에서 위로 올라오는 애니메이션
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(200)
                )
            },
            // ControlScreen 이탈 시
            exitTransition = {
                // 위에서 아래로 내려가는 애니메이션
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(200)
                )
            },
        ) {
            ControlScreen(navController = navController)
        }

        // Auth
        composable("login") {
            val context = LocalContext.current

            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true } // 뒤로가기로 돌아가지 않게
                    }
                }
            )
        }
        //composable("RegisterScreen") { RegisterScreen(navController) }


    }
}