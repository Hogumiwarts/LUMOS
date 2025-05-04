package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun DeviceRoutineCard(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f),
    showToggle: Boolean,
    cardTitle: String,
    cardSubtitle: String,
    cardIcon: @Composable (() -> Unit)? = null,
    borderStyle: BorderStroke = BorderStroke(1.dp, Color.LightGray),
    isOn: Boolean,
    onToggle: (() -> Unit)? = null,
    endPadding: Dp = 0.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(border = borderStyle, shape = RoundedCornerShape(16.dp))
            .background(Color.White)
    ) {

        cardIcon?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    // endPadding을 입력 받아서 아이콘마다 여백 지정
                    .padding(end = endPadding)
                    .size(width = 58.dp, height = 96.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                it()
            }
        }

        // 토글
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp, bottom = 18.dp, start = 15.dp), // end 패딩은 없음
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (showToggle) {
                VerticalToggle(
                    isOn = isOn,
                    onToggle = { onToggle?.invoke() }
                )
            }

            Column {
                Text(
                    text = cardTitle,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF111322),
                        letterSpacing = 0.4.sp,
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = cardSubtitle,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFA1A1A1),
                        letterSpacing = 0.4.sp,
                    )
                )
            }
        }
    }

}

@Preview(
    showBackground = true,
    widthDp = 380,
    heightDp = 862
)
@Composable
fun DeviceRoutineCardPreview() {
    var isOn by remember { mutableStateOf(true) }

    Box(modifier = Modifier.padding(16.dp)) {
        DeviceRoutineCard(
            modifier = Modifier
                .width(160.dp) // 전체 380dp 중 절반 정도로 설정
                .aspectRatio(1f), // 정사각형 비율 유지
            showToggle = true,
            cardTitle = "거실 조명",
            cardSubtitle = "조명",
            cardIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_light_off),
                    contentDescription = "조명 아이콘",
                    modifier = Modifier.size(58.dp, 96.dp)
                )
            },
            borderStyle = BorderStroke(1.dp, if (isOn) Color(0xFF4B5BA9) else Color.LightGray),
            isOn = isOn,
            onToggle = { isOn = !isOn },
            endPadding = 7.dp
        )
    }
}
