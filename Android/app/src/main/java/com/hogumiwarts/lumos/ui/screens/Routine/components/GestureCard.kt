package com.hogumiwarts.lumos.ui.screens.Routine.components

import android.health.connect.datatypes.HeightRecord
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun GestureCard(
    selectedGesture: GestureType
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x66000000), // 더 진하게 하고 싶으면 숫자 99로 바꾸기
                ambientColor = Color(0x66000000)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        contentAlignment = Alignment.CenterStart

    ) {
        Row(

        ) {
            // 좌측의 제스처 이모지
            Box(
                modifier = Modifier
                    .background(
                        Color(0xFFEBEEF8),
                        shape = RoundedCornerShape(50.dp)
                    )
                    .padding(5.dp)
            ) {
                // 제스처 아이콘
                Image(
                    painter = painterResource(id = selectedGesture.gestureiconResId),
                    contentDescription = null
                )
            }

            Spacer(modifier = Modifier.width(19.dp))

            // 우측 영역
            Column(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                // 상단 제스처 이름
                Text(
                    text = selectedGesture.gestureName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF000000),
                        )
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 하단 "제스처" 텍스트
                Text(
                    text = "제스처",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
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
fun GestureCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        GestureCard(selectedGesture = GestureType.FIST_ROTATE_180)
    }
}