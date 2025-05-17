package com.hogumiwarts.lumos.presentation.ui.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Text
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.common.AnimatedMobile
import com.hogumiwarts.lumos.presentation.ui.function.sendOpenAppMessage
import com.hogumiwarts.lumos.presentation.ui.viewmodel.DeviceViewModel
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavHostController, viewModel: DeviceViewModel = hiltViewModel()) {

    var showAnimation by remember { mutableStateOf(false) }

    val context = LocalContext.current

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val token = intent.getStringExtra("token")
                Log.d("TAG", "onReceive: $token")
                if (token != null) {
                    viewModel.saveJwt(token,"")
                    viewModel.getAccess()
                    navController.navigate("main") {
                        popUpTo("splash") { inclusive = true } // splash를 백스택에서 제거
                    }
                }
            }
        }
        context.registerReceiver(
            receiver,
            IntentFilter("TOKEN_RECEIVED"),
            Context.RECEIVER_EXPORTED // ✅ 외부 앱에서 호출할 수 없게 설정
        )

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "설명",
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "\uD83D\uDD12 로그인이 필요해요!",
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "설명",
                modifier = Modifier
                    .size(110.dp)
                    .weight(1f)
            )

            // 하단 안내 텍스트
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {

                val context = LocalContext.current
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x10FFFFFF),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable {
                            showAnimation = true

                            sendOpenAppMessage(context)
                        }
                ) {
                    Text(
                        text = "폰에서 앱 열기",
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = TextStyle(fontSize = 14.sp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showAnimation,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            AnimatedMobile()
        }

        // ✅ 2초 후 자동으로 사라지기
        LaunchedEffect(showAnimation) {
            if (showAnimation) {
                delay(2000)
                showAnimation = false
            }
        }
    }

}




@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
//        DevicesScreen()
//        LoginScreen(n)
    }
}