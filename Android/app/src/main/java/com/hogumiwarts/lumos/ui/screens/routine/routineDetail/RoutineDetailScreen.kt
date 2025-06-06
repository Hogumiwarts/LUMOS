package com.hogumiwarts.lumos.ui.screens.routine.routineDetail

import android.annotation.SuppressLint
import android.util.Log
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
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.lumos.ui.common.ConfirmCancelDialog
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceCard
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.components.GestureCard
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconType
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.delay

@Composable
fun RoutineDetailScreen(
    routineId: Long?,
    viewModel: RoutineDetailViewModel,
    onEdit: () -> Unit = {},
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

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
                onEdit = onEdit,
                onRequestDelete = { showDeleteDialog = true },
                navController
            )
        }

        RoutineDetailState.Deleted -> {
            LaunchedEffect(state) {
                if (state is RoutineDetailState.Deleted) {
                    delay(300) // ViewModel 작업 종료 대기
                    navController.popBackStack()

                    Toast.makeText(context, "루틴이 삭제되었습니다!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    ConfirmCancelDialog(
        showDialog = showDeleteDialog,
        titleText = "정말 삭제할까요?",
        bodyText = "루틴을 삭제하면 설정된 기기 동작도 모두 사라져요. 그래도 삭제하시겠어요?",
        onConfirm = {
            showDeleteDialog = false

            routineId?.let {
                viewModel.deleteRoutine(it) // 실제 삭제 요청
            } ?: run {
                Toast.makeText(context, "삭제할 루틴 ID가 유효하지 않아요!", Toast.LENGTH_SHORT).show()
            }
        },
        onCancel = { showDeleteDialog = false }
    )
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun RoutineDetailContent(
    routine: RoutineItem,
    devices: List<CommandDevice>,
    onEdit: () -> Unit,
    onRequestDelete: () -> Unit,
    navController: NavController
) {
    val deviceCount = devices.size

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .statusBarsPadding(),
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
                    modifier = Modifier.height(70.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = RoutineIconType.getResIdByName(routine.routineIcon)),
                        contentDescription = null,
                        modifier = Modifier.size(27.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = routine.routineName,
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
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("editRoutineId", routine.routineId)
                            set("editRoutineName", routine.routineName)
                            set("editRoutineIcon", routine.routineIcon)
                            set("editDevices", devices)

                            // 여기가 추가 포인트
                            if (routine.gestureId != 0L && !routine.gestureName.isNullOrBlank()) {
                                val gesture = routine.gestureDescription?.let {
                                    routine.gestureImageUrl?.let { it1 ->
                                        routine.gestureId?.let { it2 ->
                                            GestureData(
                                                routineId = routine.routineId,
                                                gestureId = it2,
                                                gestureName = routine.gestureName,
                                                gestureDescription = it,
                                                gestureImageUrl = it1,
                                                routineName = routine.routineName,
                                            )
                                        }
                                    }
                                }
                                set("selectedGesture", gesture)
                            }
                        }

                        onEdit() // routineEdit 화면으로 이동하는 Nav 로직
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700)
                    )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "삭제",
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) {
                        onRequestDelete()
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700)
                    )
                )
            }
        }

        items(devices) { device ->
            DeviceCard(
                commandDevice = device, deviceType = DeviceListType.from(device.deviceType)
            )
        }


        item {
            val gesture =
                if (routine.gestureId == 0L || routine.gestureName.isNullOrBlank()) {
                    GestureData.EMPTY
                } else {
                    routine.gestureId?.let {
                        routine.gestureDescription?.let { it1 ->
                            routine.gestureImageUrl?.let { it2 ->
                                GestureData(
                                    routineId = 1,
                                    gestureId = it,
                                    gestureName = routine.gestureName,
                                    gestureDescription = it1,
                                    gestureImageUrl = it2,
                                    routineName = routine.routineName,
                                )
                            }
                        }
                    }
                }

            if (gesture?.gestureId != 0L) {
                Log.d("routine", "🌭🌭🌭 gesture null 확인: $gesture")

                Divider(color = Color(0xFFB9C0D4), thickness = 1.dp)

                Spacer(modifier = Modifier.height(17.dp))

                if (gesture != null) {
                    GestureCard(
                        selectedGesture = gesture,
                        isEditMode = false,
                        onChangeGestureClick = { navController.navigate("gesture_select") }
                    )
                }
            }
        }
    }


}

