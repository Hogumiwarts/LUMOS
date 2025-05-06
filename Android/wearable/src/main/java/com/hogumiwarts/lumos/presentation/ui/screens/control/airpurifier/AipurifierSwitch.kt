package com.hogumiwarts.lumos.presentation.ui.screens.control.airpurifier

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Text
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.common.OnOffSwitch

@Composable
fun AipurifierSwitch(
    volumePercent: Int = 40,
    isOn: Boolean = true,
    onToggle: (Boolean) -> Unit,
    onSwipeUp: () -> Unit
) {
    val switchState = remember { mutableStateOf(isOn) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.animation_down)
    )

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111322))
    ) {
        val (title, toggle, arrow) = createRefs()

        // 제목
        Text(
            text = "교육장 공기 당담",
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 18.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toggle.top)
            }
        )

        // 볼륨 및 스위치 토글
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0x10FFFFFF))
                .clickable {
                    // 클릭 시 동작
                    onSwipeUp()
                }
                .constrainAs(toggle) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "펜속도  $volumePercent%",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "미세먼지 매우 좋음",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                OnOffSwitch(
                    checked = switchState.value,
                    onCheckedChange = {
                        switchState.value = it
                        onToggle(it)
                    }
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier.constrainAs(arrow) {
                top.linkTo(toggle.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
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