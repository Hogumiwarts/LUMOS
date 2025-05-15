package com.hogumiwarts.lumos.presentation.ui.common

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.presentation.ui.viewmodel.AirpurifierViewModel

@Composable
fun OnOffSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AirpurifierViewModel = hiltViewModel()
) {

    val isOn by viewModel.isOn.collectAsState()
    val transition = updateTransition(targetState = isOn, label = "switchTransition")


    // thumb 이동 애니메이션
    val thumbOffset by transition.animateDp(label = "thumbOffset") { isChecked ->
        if (isChecked) 30.dp else 5.dp
    }

    // 배경 색상 애니메이션
    val trackColor by transition.animateColor(label = "trackColor") { isChecked ->
        if (isChecked) Color(0xFF4CD964) else Color.DarkGray
    }

    Box(
        modifier = modifier
            .width(55.dp)
            .height(25.dp)
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable { onCheckedChange(!isOn) },
        contentAlignment = Alignment.CenterStart
    ) {
        // ON/OFF 텍스트
        Text(
            text = if (isOn) "ON" else "OFF",
            color = Color.White,
            fontSize = 10.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            textAlign = if (isOn) TextAlign.Start else TextAlign.End
        )

        // 스위치 thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(20.dp)
                .background(Color.White, shape = CircleShape)
        )
    }
}