package com.hogumiwarts.lumos.ui.screens.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel
import timber.log.Timber

@Composable
fun SettingScreen(
    authViewModel: AuthViewModel,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with gradient background
            HeaderSection()

            // Menu items
            MenuItems(authViewModel, navController)
        }
    }
}

@Composable
fun HeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF202E70),
                        Color(0xFF394587),
                        Color(0xFF4A5597),
                        Color(0xFF5661A2),
                        Color(0xFF606BAC),
                        Color(0xFF717BBC)
                    )
                ),
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
            )
            .padding(32.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "LUMOS",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // SmartThings Chip
            Row(
                modifier = Modifier
                    .background(
                        color = Color(0xffFFFFFF).copy(0.1f),
                        shape = RoundedCornerShape(20)
                    )
                    .padding(horizontal = 32.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_clova),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Text(
                    text = "SmartThings",
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun MenuItems(authViewModel: AuthViewModel, navController: NavController) {
    Column(
        modifier = Modifier.padding(24.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    // TODO:: smartthings 화면 연결
                }
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "SmartThings 계정 연결",
                fontSize = 18.sp
            )
        }


        Divider()

        // 제스처 테스트 화면
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    navController.navigate("gesture_select")
                },
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "제스처 테스트",
                fontSize = 18.sp
            )
        }

        Divider()

        // Logout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    authViewModel.logOut(
                        onSuccess = { navController.navigate("login") },
                        onFailure = {
                            Timber
                                .tag("auth")
                                .d("⚠️ 로그아웃 실패")
                        }
                    )
                },
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_edit), contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "로그아웃",
                fontSize = 18.sp
            )
        }
    }
}


//@Composable
//@Preview(showBackground = true)
//fun SettingPreview() {
//    SettingScreen()
//
//}