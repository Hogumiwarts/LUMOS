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
import com.hogumiwarts.lumos.presentation.ui.screens.SplashScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier.AipurifierSetting
import com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier.AipurifierSwitch
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.MinibigScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.MoodPlayerScreen
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DevicesScreen
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureTestScreen

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavGraph(
    navController: NavHostController
) {

    AnimatedNavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("main",
            enterTransition = {
                if(initialState.destination.route == "splash"){
                    null
                }else{
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(600)
                    )
                }

            },
            exitTransition = {
                scaleOut(
                    targetScale = 0.8f,
                    animationSpec = tween(1000)
                )
            }
            ) {
            DevicesScreen(navController)
        }
        composable("light/{tagNumber}",
            enterTransition = {

                slideInHorizontally(initialOffsetX = { 1000 },
                    animationSpec = tween(durationMillis = 600)) + fadeIn(initialAlpha = 1f)


            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }) {
            val tagNumber = it.arguments?.getString("tagNumber")?.toLongOrNull()

            if (tagNumber != null) {
                LightScreen(tagNumber = tagNumber)
            } else {
                // 예외 처리: tagNumber가 null인 경우
                Text("Invalid tag number")
            }
        }
        composable("minibig/{tagNumber}",
            enterTransition = {

                slideInHorizontally(initialOffsetX = { 1000 },
                    animationSpec = tween(durationMillis = 600)) + fadeIn(initialAlpha = 1f)


            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }) {
            val tagNumber = it.arguments?.getString("tagNumber")?.toLongOrNull()

            if (tagNumber != null) {
                MinibigScreen(tagNumber = tagNumber)
            } else {
                // 예외 처리: tagNumber가 null인 경우
                Text("Invalid tag number")
            }
        }

        composable("speaker/{tagNumber}",
            enterTransition = {

                    slideInHorizontally(initialOffsetX = { 1000 },
                        animationSpec = tween(durationMillis = 600)) + fadeIn(initialAlpha = 1f)


            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }) {
            val tagNumber = it.arguments?.getString("tagNumber")?.toLongOrNull()

            if (tagNumber != null) {
                MoodPlayerScreen(tagNumber = tagNumber)
            } else {
                // 예외 처리: tagNumber가 null인 경우
                Text("Invalid tag number")
            }
        }

        composable("airPurifier/{tagNumber}",
            enterTransition = {
                if(initialState.destination.route == "main"){
                    slideInHorizontally(initialOffsetX = { 1000 },
                        animationSpec = tween(durationMillis = 600)) + fadeIn(initialAlpha = 1f)
                }else{
                    scaleIn(
                        initialScale = 0.8f,
                        animationSpec = tween(600)
                    )
                }

            },
            exitTransition = {
                    scaleOut(
                        targetScale = 0.8f,
                        animationSpec = tween(600)
                    )
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(1000))
            }
        ) {
            val tagNumber = it.arguments?.getString("tagNumber")?.toLongOrNull()

            if (tagNumber != null) {
                AipurifierSwitch(tagNumber = tagNumber,navController)
            } else {
                // 예외 처리: tagNumber가 null인 경우
                Text("Invalid tag number")
            }
        }
        composable(
            route = "AipurifierSetting",
            enterTransition = {
                slideInHorizontally(initialOffsetX = { 1000 },
                    animationSpec = tween(durationMillis = 600)) + fadeIn(initialAlpha = 1f)
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { 1000 },
                    animationSpec = tween(durationMillis = 600))
            }
        ) {
            AipurifierSetting()
        }



    }
}