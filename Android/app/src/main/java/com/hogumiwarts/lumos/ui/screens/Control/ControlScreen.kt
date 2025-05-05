
package com.hogumiwarts.lumos.ui.screens.Control

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hogumiwarts.lumos.ui.common.CommonTopBar

@Composable
fun ControlScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CommonTopBar(
                barTitle = "기기 이름",
                onBackClick = {
                    navController.popBackStack()
                },
                isAddBtnVisible = false,
                onAddClick = {})
        }
    ) { innerPadding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.LightGray)
        ) {

            Text(
                text = "ㅋ 제어 화면 ㅋ",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

        }
    }
}