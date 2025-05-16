package com.hogumiwarts.lumos.presentation.ui.screens.control.speaker

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.domain.model.audio.AudioStatusData
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.common.OnOffSwitch

// 무드 플레이어 화면 (첫 번째 화면)
@Composable
fun MoodPlayerSwitch(
    data: AudioStatusData,
    volumePercent: Int = 40,
    isOn: Boolean = true,
    onToggle: (Boolean) -> Unit,
    onSwipeUp: () -> Unit
) {

    var isPlaying by remember { mutableStateOf(data.activated) }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                var totalDrag = 0f
                detectVerticalDragGestures(
                    onDragEnd = {
                        if (totalDrag < -50f) {
                            onSwipeUp() // 위로 스와이프 시 전환
                        }
                        totalDrag = 0f
                    },
                    onVerticalDrag = { _, dragAmount ->
                        totalDrag += dragAmount
                    }
                )
            }
    ) {
        val (title, toggle, arrow) = createRefs()

        // 제목
        Text(
            text = data.deviceName,
            color = Color.White,
            fontSize = 18.sp,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 18.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toggle.top)
            }
        )

        // 플레이어 제어
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(30.dp))
                .constrainAs(toggle) {
                    top.linkTo(parent.top, margin = 18.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            contentAlignment = Alignment.CenterStart
        ) {

            AsyncImage(
                model = data.audioImg,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(30.dp)),
                contentScale = ContentScale.Crop,
                alpha = 0.70f
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.5f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.padding(start = 6.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = data.audioName,
                        color = Color.White,
                        fontSize = 22.sp
                    )

                    Text(
                        text = data.audioArtist,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )
                }

                // 재생/일시정지 버튼
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isPlaying = !isPlaying
                        },
                    contentAlignment = Alignment.Center
                ) {

                    if (isPlaying) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "일시정지",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pause),
                            contentDescription = "재생",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

        // 아래 버튼
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier
                .constrainAs(arrow) {
                    bottom.linkTo(parent.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(toggle.bottom)
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
