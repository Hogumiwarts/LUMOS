package com.hogumiwarts.lumos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.ui.navigation.BottomNavItem
import com.hogumiwarts.lumos.ui.navigation.BottomNavigation
import com.hogumiwarts.lumos.ui.navigation.NavGraph

@Composable
fun MainScreen(deviceId: Long, deviceType: String) {
    val navController = rememberNavController()

    // 현재 백스택 엔트리 가져오기
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // 메인 화면에 해당하는 라우트 목록
    val mainScreens = listOf(
        BottomNavItem.Home.route,
        BottomNavItem.Info.route,
        BottomNavItem.Routine.route,
        BottomNavItem.Settings.route
    )

    // 네비게이션 바를 표시할 화면인지 확인
    val isNavOn = currentDestination?.route in mainScreens

    @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
    Scaffold(
        bottomBar = {
            if (isNavOn) {
                BottomNavigation(navController = navController)
            }
        },
        floatingActionButton = {
            if (isNavOn) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("findDeviceScreen")

                    },
                    modifier = Modifier
                        .size(72.dp)
                        .offset(y = 52.dp),
                    shape = CircleShape,
                    containerColor = Color.White,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_main),  // 중앙 로고
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center

    ) { innerPadding ->
        NavGraph(
            deviceId= deviceId, deviceType= deviceType,
            navController = navController,
            modifier = Modifier.fillMaxSize()
        )
    }
}