package com.hogumiwarts.lumos.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hogumiwarts.lumos.presentation.ui.screens.SplashScreen
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
            DevicesScreen()
        }
    }
}