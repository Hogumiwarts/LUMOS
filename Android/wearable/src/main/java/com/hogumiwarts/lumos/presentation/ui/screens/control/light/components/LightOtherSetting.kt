package com.hogumiwarts.lumos.presentation.ui.screens.control.light.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme

@Composable
fun LightOtherSetting(
    onSwipeDown: () -> Unit,
){
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111322))
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (totalDrag > 50f) {
                            onSwipeDown() // 위로 스와이프 시 전환
                        }
                        totalDrag = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            }
    )  {

        val (title, arrow) = createRefs()
        // 상단 텍스트
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.constrainAs(title) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }){

            Text(
                text = "더 세밀한 제어가 필요하다면,",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W100),
                color = Color(0xFFF9F9F9),
            )
            Text(
                text = "휴대폰애서 계속 설정해보세요\uD83E\uDE84",
                style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.W100),
                color = Color(0xFFF9F9F9),
            )
        }




        // 하단 안내 텍스트
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier
                .constrainAs(arrow) {
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(title.bottom)
                }
        ) {
            Text(
                text = "폰에서 세부 제어",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = TextStyle(fontSize = 14.sp)
            )
        }

    }
}

// 🧪 Wear OS 에뮬레이터에서 미리보기 지원
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview1() {
    LUMOSTheme {
        LightOtherSetting({})
    }
}