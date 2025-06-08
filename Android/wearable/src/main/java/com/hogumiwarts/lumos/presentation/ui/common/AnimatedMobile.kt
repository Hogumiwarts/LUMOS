package com.hogumiwarts.lumos.presentation.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.lumos.R

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.draw.clip

@Composable
fun AnimatedMobile(modifier: Modifier = Modifier){
    Box(
        modifier = modifier
            .size(90.dp)
            .padding(bottom = 40.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A1A).copy(alpha = 0.9f))

    ) {
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(R.raw.am_mobile_white)
        )
        // 아래 버튼
        // 4. Other Setting
        // Lottie 애니메이션: 하단 고정
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier
                .size(40.dp)
                .padding(1.dp)
                .align(Alignment.Center)
        )
    }
}