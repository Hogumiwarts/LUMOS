package com.hogumiwarts.lumos.ui.screens.routine.routineList

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.hogumiwarts.lumos.ui.common.CommonTopBar
import com.hogumiwarts.lumos.ui.common.DeviceRoutineCard
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.domain.model.routine.Routine
import com.hogumiwarts.lumos.ui.common.ConfirmCancelDialog
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconType
import com.hogumiwarts.lumos.ui.screens.routine.routineDetail.RoutineDetailViewModel
import timber.log.Timber

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onRoutineClick: (Routine) -> Unit = {},
    viewModel: RoutineViewModel = hiltViewModel(),
    routineDetailViewModel: RoutineDetailViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    val (selectedRoutine, setSelectedRoutine) = remember { mutableStateOf<Routine?>(null) }
    var isSheetVisible by remember { mutableStateOf(false) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    val routineList by viewModel.routineList.collectAsState()
    val context = LocalContext.current

    // 루틴 불러오기 한 번 실행
    LaunchedEffect(Unit) {
        viewModel.getRoutineList()
    }

    routineList.forEach {
        Timber.tag("routine").d("✅ 루틴 확인: ${it.routineName}")
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        //상단 TopBar
        CommonTopBar(
            barTitle = "나의 루틴",
            onBackClick = { onBackClick() },
            isRightBtnVisible = true,
            onRightBtnClick = onAddClick,
            isBackBtnVisible = false
        )

        // 루틴 카드 목록 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxHeight()
        ) {
            items(routineList) { routine ->
                routine.gestureName.let {
                    DeviceRoutineCard(
                        modifier = Modifier
                            .aspectRatio(1.05f)
                            .clip(RoundedCornerShape(10.dp))
                            .combinedClickable(
                                onClick = { onRoutineClick(routine) },
                                onLongClick = {
                                    setSelectedRoutine(routine)
                                    coroutineScope.launch { isSheetVisible = true }
                                }
                            ),
                        showToggle = false, // 토글 X
                        cardTitle = routine.routineName,
                        cardSubtitle = routine.gestureName ?: "제스처 없음",
                        isOn = false,
                        iconSize = DpSize(80.dp, 80.dp),
                        cardIcon = { size ->
                            Image(
                                painter = painterResource(
                                    id = RoutineIconType.getResIdByName(routine.routineIcon)
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .size(size)
                            )
                        },
                        endPadding = 3.dp,
                        isActive = true,
                    )
                }
            }
        }
    }

    // 삭제 확인 다이얼로그
    if (showDeleteDialog) {
        ConfirmCancelDialog(
            showDialog = showDeleteDialog,
            titleText = "정말 삭제할까요?",
            bodyText = "루틴을 삭제하면 설정된 기기 동작도 모두 사라져요. 그래도 삭제하시겠어요?",
            onConfirm = {
                showDeleteDialog = false
                selectedRoutine?.routineId?.let {
                    routineDetailViewModel.deleteRoutine(it)
                    viewModel.getRoutineList()
                    isSheetVisible = false
                } ?: run {
                    Toast.makeText(context, "삭제할 루틴 ID가 유효하지 않아요!", Toast.LENGTH_SHORT).show()
                }
            },
            onCancel = {
                showDeleteDialog = false
            }
        )
    }

    // 바텀 시트 설정
    if (isSheetVisible && selectedRoutine != null) {
        ModalBottomSheet(
            onDismissRequest = { isSheetVisible = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pen),
                        contentDescription = null,
                        Modifier.size(25.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "수정하기",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF111322),
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onRoutineClick(selectedRoutine)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(27.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = null,
                        Modifier.size(25.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "삭제하기",
                        style = TextStyle(
                            fontSize = 20.sp,
                            lineHeight = 24.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF111322),
                            textAlign = TextAlign.Center,
                        ),
                        modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showDeleteDialog = true
                        }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun RoutineScreenPreview() {
    RoutineScreen()
}
