package com.hogumiwarts.lumos.ui.screens.routine.routineList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineItem
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
import androidx.compose.ui.Alignment

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RoutineScreen(
    routines: List<RoutineItem> = RoutineItem.sample,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onRoutineClick: (RoutineItem) -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val coroutineScope = rememberCoroutineScope()
    val (selectedRoutine, setSelectedRoutine) = remember { mutableStateOf<RoutineItem?>(null) }
    var isSheetVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
    ) {
        //상단 TopBar
        CommonTopBar(
            barTitle = "나의 루틴",
            onBackClick = { onBackClick },
            isRightBtnVisible = true,
            onRightBtnClick = onAddClick
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
            items(routines) { routine ->
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
                    cardTitle = routine.title,
                    cardSubtitle = routine.subtitle,
                    isOn = false,
                    iconSize = DpSize(80.dp, 80.dp),
                    cardIcon = { size ->
                        Image(
                            painter = painterResource(id = routine.iconResId),
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
                        )
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
                        )
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
