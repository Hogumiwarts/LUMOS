package com.hogumiwarts.lumos.ui.screens.Routine.routineDetail

import android.graphics.drawable.Icon
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineDevice
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.ui.screens.Routine.components.DeviceCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.GestureCard
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineDetailScreen(
    routineDevices: List<RoutineDevice> = RoutineDevice.sample, // 루틴별 기기 정보
    routineItem: List<RoutineItem> = RoutineItem.sample // 루틴 리스트
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 40.dp, start = 28.dp, end = 28.dp)
    ) {
        val deviceCount = routineDevices.size

        // todo: 추후 하드코딩으로 넣어놓은거 api 연동하기
        // todo: 17.dp 씩 여백 있는데 spaceBy로 한 번에 설정하기

        // TopBar
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.align(Alignment.Center)
            ) {
                // 아이콘
                Image(
                    painter = painterResource(id = R.drawable.ic_moon_sleep),
                    contentDescription = null,
                    modifier = Modifier.size(27.dp),
                    alignment = Alignment.Center
                )

                Spacer(modifier = Modifier.width(6.dp))

                // 루틴 이름
                Text(
                    text = routineItem[0].title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF000000),
                        fontFamily = nanum_square_neo,
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(17.dp))

        // 리스트 정보
        Row(

        ) {
            // 기기 개수
            Text(
                text = "$deviceCount" + "개",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    letterSpacing = 0.4.sp,
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // 수정 버튼
            Text(
                text = "수정",
                modifier = Modifier.clickable {
                    //todo: 수정 로직 실행
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    letterSpacing = 0.4.sp,
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            // 삭제 버튼
            Text(
                text = "삭제",
                modifier = Modifier.clickable {
                    //todo: 삭제 로직 실행
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF000000),
                    letterSpacing = 0.4.sp,
                )
            )
        }

        Spacer(modifier = Modifier.height(17.dp))

        // 기기 리스트
        for (i in 0 until deviceCount) {
            DeviceCard(routineDevices[i])
        }

        Spacer(modifier = Modifier.height(17.dp))

        // 구분선
        Divider(
            color = Color(0xFFB9C0D4),
            thickness = 1.dp
        )

        Spacer(modifier = Modifier.height(17.dp))

        // 제스처 카드
        GestureCard()
    }
}


@Preview(showBackground = true)
@Composable
fun RoutineDetailScreenPreview() {
    RoutineDetailScreen()
}
