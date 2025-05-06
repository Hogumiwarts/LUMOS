package com.hogumiwarts.lumos.presentation.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R

@Composable
fun AnimatedToggleButton(
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier
) {
    // Lottie 애니메이션 로딩
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.toggle_switch_animation)
    )

    val lottieAnimatable = rememberLottieAnimatable()
    val coroutineScope = rememberCoroutineScope()

    // 상태 변경 시 애니메이션 실행
    LaunchedEffect(isOn) {
        composition?.let {
            val (start, end) = if (isOn) {
                0f to 0.5f // ON → 절반만 재생
            } else {
                0.5f to 1f // OFF → 절반 이후 재생
            }

            lottieAnimatable.animate(
                composition = it,
                clipSpec = LottieClipSpec.Progress(start, end),
                speed = 2f // 빠르게 재생
            )
        }
    }

    // 터치 가능한 애니메이션 박스
    Box(
        modifier = modifier
            .clickable(
                indication = null, // 기본 ripple 제거
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onToggle(!isOn) // 토글 상태 변경
            },
        contentAlignment = Alignment.Center,
        
    ) {
        // 실제 애니메이션 그리기
        LottieAnimation(
            modifier = Modifier
                .width(168.dp)
                .height(80.dp),
            composition = composition,
            progress = { lottieAnimatable.progress }
        )
    }
}