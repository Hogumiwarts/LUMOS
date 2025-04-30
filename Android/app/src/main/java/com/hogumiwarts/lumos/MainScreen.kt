package com.hogumiwarts.lumos

import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.ui.navigation.BottomNavigation
import com.hogumiwarts.lumos.ui.navigation.NavGraph

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation(navController = navController)
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                modifier = Modifier
                    .size(80.dp)
                    .offset(y = 52.dp),
                shape = CircleShape,
                containerColor = Color.White
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_main_button),  // 중앙 로고
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center

    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}