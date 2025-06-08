package com.hogumiwarts.lumos.ui.screens.routine.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import coil.compose.AsyncImage
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.selects.select

@Composable
fun GestureCard(
    selectedGesture: GestureData,
    isEditMode: Boolean,
    onChangeGestureClick: () -> Unit
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
                    .padding(2.dp)
                    .size(50.dp)
            ) {
                // 제스처 아이콘
                AsyncImage(
                    model = selectedGesture.gestureImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
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
                onClick = onChangeGestureClick,
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

@Preview(showBackground = true, widthDp = 380, heightDp = 100)
@Composable
fun GestureCardPreview() {
    GestureCard(
        selectedGesture = GestureData(
            gestureId = 1,
            gestureName = "두 번 박수",
            gestureDescription = "가슴 앞에서 두 번 박수칩니다",
            gestureImageUrl = "https://example.com/sample1.png",
            routineName = "취침 루틴",
            routineId = 1
        ),
        isEditMode = true,
        onChangeGestureClick = {}
    )
}
