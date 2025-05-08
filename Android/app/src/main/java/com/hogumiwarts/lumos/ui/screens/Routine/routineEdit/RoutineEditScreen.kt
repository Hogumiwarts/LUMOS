package com.hogumiwarts.lumos.ui.screens.Routine.routineEdit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.Routine.components.DeviceCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.GestureCard
import com.hogumiwarts.lumos.ui.screens.Routine.components.GestureType
import com.hogumiwarts.lumos.ui.screens.Routine.components.RoutineIconList
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineEditScreen(
    routineId: String?,
    viewModel: RoutineEditViewModel
) {
    val selectedIcon by viewModel.selectedIcon.collectAsState()

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
                Text(
                    text = "루틴 수정",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = nanum_square_neo
                )
            }

        }

        // 아이콘 선택
        item {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 제목
                Text(
                    text = "아이콘",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF000000),
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                // 아이콘 선택 UI
                RoutineIconList(
                    selectedIcon = selectedIcon,
                    onIconSelected = { icon ->
                        viewModel.selectIcon(icon)
                    }
                )
            }
        }

        // 루틴 이름

        // 적용 기기

        // 제스처 선택

        // 수정 버튼

    }
}

@Preview(showBackground = true)
@Composable
fun RoutineEditScreenPreview() {
    val fakeViewModel = remember { RoutineEditViewModel() }

    RoutineEditScreen(
        routineId = "1",
        viewModel = fakeViewModel
    )
}