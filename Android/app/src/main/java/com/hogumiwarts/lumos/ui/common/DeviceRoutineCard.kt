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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
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
    cardIcon: @Composable (DpSize) -> Unit = {},
    iconSize: DpSize = DpSize(58.dp, 96.dp),
    borderStyle: BorderStroke = BorderStroke(1.dp, Color.LightGray),
    isOn: Boolean,
    onToggle: (() -> Unit)? = null,
    endPadding: Dp = 0.dp,
    isActive: Boolean // 활성화 여부 -> smartthings 에서 받아와야 함
) {

    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x33000000),
                ambientColor = Color(0x33000000)
            )
            .background(Color.White, shape = RoundedCornerShape(10.dp))
            .border(border = borderStyle, shape = RoundedCornerShape(10.dp))
    ) {
        // 비활성화인 기기의 경우 카드를 회색으로 처리해서 선택 불가능함을 표시

        cardIcon?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    // endPadding을 입력 받아서 아이콘마다 여백 지정
                    .padding(end = endPadding)
                    .size(iconSize)
                    .graphicsLayer {
                        alpha = 1f
                    },
                contentAlignment = Alignment.TopEnd
            ) {
                cardIcon(iconSize)

            }
        }

        // 토글
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 12.dp, bottom = 18.dp, start = 15.dp), // end 패딩은 없음
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (showToggle) {
                VerticalToggle(
                    isOn = isOn,
                    onToggle = { onToggle?.invoke() }
                )
            } else {
                Spacer(modifier = Modifier.height(53.dp))
            }

            Column {
                Text(
                    text = cardTitle,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight(800),
                        fontFamily = nanum_square_neo,
                        color = if (isActive) colorResource(id = R.color.black_primary) else Color(
                            0xFF606069
                        )
                    )

                )

                Spacer(modifier = Modifier.height(1.dp))


                Text(
                    text = cardSubtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = nanum_square_neo,
                        color = when {
                            !isActive -> Color(0xFFB6B6B6)
                            cardSubtitle == "제스처 없음" -> Color(0xFFE0E0E0)
                            else -> colorResource(id = R.color.gray_light)
                        }
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
            showToggle = false,
            cardTitle = "거실 조명",
            cardSubtitle = "조명",
            cardIcon = {
                Image(
                    painter = painterResource(id = R.drawable.ic_light_off),
                    contentDescription = "조명 아이콘",
                    modifier = Modifier.size(58.dp, 96.dp)
                )
            },
            borderStyle = BorderStroke(
                1.dp,
                if (isOn) colorResource(id = R.color.point_color) else Color.LightGray
            ),
            isOn = isOn,
            onToggle = { isOn = !isOn },
            endPadding = 7.dp,
            isActive = true
        )
    }
}
