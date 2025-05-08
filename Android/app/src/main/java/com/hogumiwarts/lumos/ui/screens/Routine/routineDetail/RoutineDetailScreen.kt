package com.hogumiwarts.lumos.ui.screens.Routine.routineDetail

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.ui.screens.Routine.components.DeviceCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.GestureCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.GestureType
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineDetailScreen(
    routineId: String?,
    routineDevices: List<RoutineDevice> = RoutineDevice.sample, // 루틴별 기기 정보
    routineItem: List<RoutineItem> = RoutineItem.sample, // 루틴 리스트
    onBack: () -> Unit = {},
    viewModel: RoutineDetailViewModel,
    onEdit: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(routineId) {
        if (routineId != null) {
            viewModel.loadRoutine(routineId)
        } else {
            Toast.makeText(context, "\uD83D\uDE22 루틴 정보를 찾을 수 없습니다!", Toast.LENGTH_SHORT).show()
        }
    }


    when (state) {
        is RoutineDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is RoutineDetailState.Error -> {
            val error = (state as RoutineDetailState.Error).message
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = error)
                // TODO: Snackbar 또는 자동 뒤로가기 처리
            }
        }

        is RoutineDetailState.Success -> {
            val data = state as RoutineDetailState.Success
            RoutineDetailContent(
                routine = data.routine,
                devices = data.devices,
                onEdit = onEdit
            )
        }

    }


}

@Composable
fun RoutineDetailContent(
    routine: RoutineItem,
    devices: List<RoutineDevice>,
    onEdit: () -> Unit
) {
    val deviceCount = devices.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .statusBarsPadding(),
        contentPadding = PaddingValues(vertical = 25.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        item {
            // TopBar
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_moon_sleep),
                        contentDescription = null,
                        modifier = Modifier.size(27.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = routine.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = nanum_square_neo
                    )
                }
            }

        }

        item {
            // 리스트 정보
            Row {
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

                Text(
                    "수정",
                    modifier = Modifier.clickable {
                        onEdit()
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700)
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "삭제",
                    modifier = Modifier.clickable {
                        // todo: 삭제 화면으로 이동
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700)
                    )
                )
            }
        }

        items(devices) { device ->
            DeviceCard(routineDevice = device)
        }

        item {
            Divider(color = Color(0xFFB9C0D4), thickness = 1.dp)
        }

        item {
            GestureCard(selectedGesture = GestureType.DOUBLE_CLAP)
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun RoutineDetailScreenPreview() {
//    RoutineDetailScreen()
//}
