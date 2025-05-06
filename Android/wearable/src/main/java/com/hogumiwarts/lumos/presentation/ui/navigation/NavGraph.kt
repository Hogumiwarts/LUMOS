package com.hogumiwarts.lumos.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.presentation.ui.screens.SplashScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightScreen
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DevicesScreen

@Composable
fun NavGraph(
    navController: NavHostController
) {

    NavHost(navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen {
                navController.navigate("main") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
        composable("main") {
            DevicesScreen(navController)
        }
        composable("light/{tagNumber}") {
            val tagNumber = it.arguments?.getString("tagNumber")?.toLongOrNull()

            if (tagNumber != null) {
                LightScreen(tagNumber = tagNumber)
            } else {
                // 예외 처리: tagNumber가 null인 경우
                Text("Invalid tag number")
            }
        }
    }
}