package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun TertiaryButton(
    buttonText: String
) {
    Box(
        modifier = Modifier
            .padding(vertical = 15.dp, horizontal = 35.dp)
            .fillMaxWidth()
            .height(56.dp)
            .background(color = Color(0x1AFFFFFF), shape = RoundedCornerShape(size = 15.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buttonText,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 25.sp,
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight(400),
                color = Color(0xFFFFFFFF),
                textAlign = TextAlign.Center,
            )
        )
    }
}