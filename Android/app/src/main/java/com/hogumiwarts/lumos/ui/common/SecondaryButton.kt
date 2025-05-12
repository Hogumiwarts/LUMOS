package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun SecondaryButton(
    buttonText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(45.dp)
            .background(
                colorResource(id = R.color.white),
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                width = 1.5.dp,
                color = Color(0xFF3E4784),
                shape = RoundedCornerShape(size = 8.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center,

        ) {
        Text(
            text = buttonText,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight(800),
                color = Color(0xFF3E4784),
                textAlign = TextAlign.Center,
            )
        )
    }
}

@Preview(showBackground = false)
@Composable
private fun SecondaryButtonPreview() {
    SecondaryButton(buttonText = "수정하기", onClick = {})
}