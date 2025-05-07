package com.hogumiwarts.lumos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.ui.navigation.BottomNavigation
import com.hogumiwarts.lumos.ui.navigation.NavGraph
import com.hogumiwarts.lumos.ui.screens.Gesture.GestureScreen
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        // 시스템 바 영역까지 앱이 확장되도록
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 상태바, 네비게이션 바 배경을 투명하게 설정
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // 상태바, 네비게이션 바 아이콘 색상 설정 (false: 아이콘 흰색)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false


        setContent {
            LUMOSTheme {

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = Color.Transparent
                ) {
                    GestureScreen()
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {

    }
}