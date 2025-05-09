package com.hogumiwarts.lumos.ui.screens.Routine.components

import android.health.connect.datatypes.HeightRecord
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun GestureCard(
    selectedGesture: GestureType,
    isEditMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(
                elevation = 9.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x55000000), // 더 진하게 하고 싶으면 숫자 99로 바꾸기
                ambientColor = Color(0x55000000)
            )
            .border((1).dp, color = Color(0xFFF2F2F2), shape = RoundedCornerShape(10.dp))
            .graphicsLayer {
                clip = false
            }
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 16.dp, vertical = 13.dp),
        contentAlignment = Alignment.CenterStart

    ) {
        Row(
            modifier = Modifier.fillMaxSize()
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
                        fontSize = 14.sp,
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
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF000000),
                    )

                )
            }
        }

        // 변경 버튼
        if (isEditMode) {
            Button(
                onClick = {
                    /*TODO: 기기 목록 화면 띄우기*/
                },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier
                    .background(
                        Color(0xFF3E4784),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .width(75.dp)
                    .height(25.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Text(
                    text = "변경하기",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
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
        GestureCard(
            selectedGesture = GestureType.FIST_ROTATE_180,
            true
        )
    }
}