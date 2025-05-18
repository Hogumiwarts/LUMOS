package com.hogumiwarts.lumos.presentation.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.wear.compose.material.Text
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.hogumiwarts.lumos.presentation.ui.screens.LoginScreen
import com.hogumiwarts.lumos.presentation.ui.screens.SplashScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier.AipurifierSetting
import com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier.AipurifierSwitch
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.SwitchScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.MoodPlayerScreen
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DevicesScreen
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureTestScreen
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    navController: NavHostController
) {
    // Accompanist 라이브러리 기반 애니메이션 지원 NavHost 사용
    AnimatedNavHost(navController, startDestination = "splash") {

        // 🔸 SplashScreen → Main으로 이동
        composable("splash") {
            SplashScreen(onTimeout = {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true } // splash를 백스택에서 제거
                }
            },
                goLogin = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true } // splash를 백스택에서 제거
                    }
                })
        }

        // 🔸 DevicesScreen (메인 화면)
        composable(
            "main",
            enterTransition = {
                if (initialState.destination.route == "splash") {
                    null // splash → main 전환 시엔 애니메이션 없음
                } else {
                    scaleIn(initialScale = 0.8f, animationSpec = tween(600))
                }
            },
            exitTransition = {
                scaleOut(targetScale = 0.8f, animationSpec = tween(1000))
            }
        ) {
            DevicesScreen(navController)
        }

        composable(
            "login",
        ) {
            LoginScreen(navController)
        }

        // 🔸 Light 기기 제어 화면
        composable("light/{deviceId}",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(600)) +
                        fadeIn(initialAlpha = 1f)
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }
        ) {
            val deviceId = it.arguments?.getString("deviceId")?.toLongOrNull()

            if(deviceId != null){
                LightScreen(deviceId = deviceId)
            }else{
                Text(text = "오류가 발생했습니다.")
            }
        }

        // 🔸 Minibig 기기 제어 화면
        composable("minibig/{deviceId}",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(600)) +
                        fadeIn(initialAlpha = 1f)
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }
        ) {
            val deviceId = it.arguments?.getString("deviceId")?.toLongOrNull()

            if(deviceId != null){
                SwitchScreen(deviceId = deviceId)
            }else{
                Text(text = "오류가 발생했습니다.")
            }
            

        }

        // 🔸 Speaker 제어 화면
        composable("speaker/{deviceId}",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(600)) +
                        fadeIn(initialAlpha = 1f)
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }
        ) {
            val deviceId = it.arguments?.getString("deviceId")?.toLongOrNull()
            if (deviceId != null) {
                MoodPlayerScreen(deviceId = deviceId)
            }
        }

        // 🔸 공기청정기 제어 화면
        composable("airPurifier/{deviceId}",
            enterTransition = {
                if (initialState.destination.route == "main") {
                    slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(600)) +
                            fadeIn(initialAlpha = 1f)
                } else {
                    scaleIn(initialScale = 0.8f, animationSpec = tween(600))
                }
            },
            exitTransition = {
                scaleOut(targetScale = 0.8f, animationSpec = tween(600))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }
        ) {
            val deviceId = it.arguments?.getString("deviceId")?.toLongOrNull()

                    if (deviceId != null) {
                        AipurifierSwitch(deviceId = deviceId.toLong(), navController)
                    }



        }

        // 🔸 공기청정기 세팅 화면 (파라미터 없음)
        composable("AipurifierSetting/{type}/{deviceId}",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(600)) +
                        fadeIn(initialAlpha = 1f)
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(600))
            }
        ) {
            val type = it.arguments?.getString("type")
            val deviceId = it.arguments?.getString("deviceId")

            if (type != null && deviceId != null) {
                AipurifierSetting(deviceId = deviceId.toLong(), type = type)
            }

        }
    }
}
