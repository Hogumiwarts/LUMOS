package com.hogumiwarts.lumos.presentation.ui.screens.error

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.function.sendOpenLightMessage

@Composable
fun ErrorInternetScreen(onclick:()->Unit){
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_not_network), // 🔸 여기에 drawable 리소스 ID 입력
                contentDescription = "설명",
                modifier = Modifier.size(40.dp),
                contentScale = ContentScale.Crop
            )
            Text(text = "인터넷이 연결되지", fontSize = 16.sp, color = Color.White)
            Text(text = "않았습니다.", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.size(4.dp))
            Text(text = "WIFI 또는 셀룰러 연결 상태를", fontSize = 12.sp, color = Color(0xff9D9D9D))
            Text(text = "확인해 주세요.", fontSize = 12.sp, color = Color(0xff9D9D9D))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0x10FFFFFF),
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable {
                        onclick()
                    }
            ) {
                Text(
                    text = "확인",
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp),
                    style = TextStyle(fontSize = 14.sp)
                )
            }
        }
    }
}

// 프리뷰 (Wear OS 장치에서 시스템 UI 포함 화면 미리보기)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
        ErrorInternetScreen({})
    }
}