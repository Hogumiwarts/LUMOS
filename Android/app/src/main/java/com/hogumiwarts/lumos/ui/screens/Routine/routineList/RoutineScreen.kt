package com.hogumiwarts.lumos.ui.screens.Routine.routineList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.hogumiwarts.lumos.ui.common.CommonTopBar
import com.hogumiwarts.lumos.ui.common.DeviceRoutineCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineItem


@Composable
fun RoutineScreen(
    routines: List<RoutineItem> = RoutineItem.sample,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onRoutineClick: (RoutineItem) -> Unit = {}
) {
    // TODO: ViewModel에서 루틴 데이터를 받아오는 구조로 변경 예정

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 40.dp)
    ) {
        //상단 TopBar
        CommonTopBar(
            barTitle = "나의 루틴",
            onBackClick = { onBackClick },
            isAddBtnVisible = true,
            onAddClick = onAddClick
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
                        .clickable{onRoutineClick(routine)},
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
                    endPadding = 3.dp
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun RoutineScreenPreview() {
    RoutineScreen()
}
