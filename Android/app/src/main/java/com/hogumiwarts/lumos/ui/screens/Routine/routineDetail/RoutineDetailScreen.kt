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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.ui.screens.Routine.components.DeviceCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.GestureCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.GestureType
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineDetailScreen(
    routineId: String?,
    routineDevices: List<RoutineDevice> = RoutineDevice.sample, // 루틴별 기기 정보
    routineItem: List<RoutineItem> = RoutineItem.sample // 루틴 리스트
) {
    val currentRoutine = routineItem.find { it.id.toString() == routineId } ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(top = 30.dp, start = 28.dp, end = 28.dp)
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
                    text = currentRoutine.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = nanum_square_neo
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
        Box(
            modifier = Modifier
                .graphicsLayer {
                    clip = false
                }
        ) {
            LazyColumn(
                contentPadding = PaddingValues(top = 10.dp, bottom = 30.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                items(routineDevices) { device ->
                    DeviceCard(routineDevice = device)
                }
            }
        }


        // 구분선
        Divider(
            color = Color(0xFFB9C0D4),
            thickness = 1.dp
        )


        // 제스처 카드
        // todo: 선택된 제스처 api 연결
        Box(
            modifier = Modifier
                .padding(top = 30.dp)
        ) {
            GestureCard(selectedGesture = GestureType.DOUBLE_CLAP)
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun RoutineDetailScreenPreview() {
//    RoutineDetailScreen()
//}
