package com.hogumiwarts.lumos.ui.screens.auth.smartthings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.TertiaryButton
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.delay
import kotlin.coroutines.coroutineContext

@Composable
fun SmartThingsLoadingScreen(

) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1C3A)),
        contentAlignment = Alignment.Center
    ) {
        val visible1 = remember { mutableStateOf(false) }
        val visible2 = remember { mutableStateOf(false) }
        val visible3 = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(500)
            visible3.value = true
            delay(500)
            visible2.value = true
            delay(500)
            visible1.value = true
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(238.dp))

            // 로딩 애니메이션
            LottieAnimation(
                modifier = Modifier.size(200.dp),
                composition = rememberLottieComposition(
                    spec = LottieCompositionSpec.Asset("smartthings_linking.json")
                ).value,
                iterations = LottieConstants.IterateForever,
                isPlaying = true
            )

            Spacer(modifier = Modifier.height(20.dp))


            // 로딩 중 안내 텍스트
            Text(
                text = "SmartThings 계정에 연결 중입니다 ..",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFFFFFFF),
                    letterSpacing = 0.4.sp,
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 하단 단계별 안내 UI
            AnimatedVisibility(
                visible = visible3.value,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                TertiaryButton("🪄 기기 정보 불러오는 중")
                Spacer(modifier = Modifier.height(19.dp))
            }

            AnimatedVisibility(
                visible = visible2.value,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                TertiaryButton("🪄 권한 허용 대기 중")
                Spacer(modifier = Modifier.height(19.dp))
            }

            AnimatedVisibility(
                visible = visible1.value,
                enter = fadeIn() + slideInVertically { it / 2 }
            ) {
                TertiaryButton("🪄 SmartThings 계정 인증 요청")
                Spacer(modifier = Modifier.height(19.dp))
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSmartThingsLoadingScreen() {
    SmartThingsLoadingScreen()
}
