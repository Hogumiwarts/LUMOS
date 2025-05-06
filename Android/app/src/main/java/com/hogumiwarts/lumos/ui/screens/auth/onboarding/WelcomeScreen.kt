package com.hogumiwarts.lumos.ui.screens.auth.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.auth.components.GradientButton
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1C3A), Color(0xFF251744))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 배경 이미지
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.bg_onboarding_space),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            // 로고 및 앱 한 줄 소개
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LUMOS",
                    style = TextStyle(
                        fontSize = 60.sp,
                        fontFamily = FontFamily(Font(R.font.crimsontext_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )


                Text(
                    text = "손 끝으로 제어하는 나만의 스마트 홈",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = nanum_square_neo,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 하단 버튼들
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 회원가입 버튼
                GradientButton(
                    onClick = onStartClick,
                    inputText = "시작하기"
                )

                Spacer(modifier = Modifier.height(15.dp))

                // 로그인 텍스트(버튼)
                Row(){
                    Text(
                        text = "이미 계정이 있나요?",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Light,
                            fontFamily = nanum_square_neo,
                            color = Color.White
                        )
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "로그인",
                        modifier = Modifier.clickable { onLoginClick() },
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = nanum_square_neo,
                            color = Color(0xFF717BBC)
                        )
                    )

                    Spacer(modifier = Modifier.height(80.dp))

                }
            }
        }
    }
}

@Composable
@androidx.compose.ui.tooling.preview.Preview(
    showBackground = true,
    showSystemUi = true,
    name = "WelcomeScreen Preview",
    widthDp = 360,
    heightDp = 800
)
fun WelcomeScreenPreview() {
    WelcomeScreen(
        onStartClick = {},
        onLoginClick = {}
    )
}
