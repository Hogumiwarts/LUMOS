package com.hogumiwarts.lumos.ui.screens.Routine.routineDeviceList

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.ui.common.CommonDialog
import com.hogumiwarts.lumos.ui.common.DeviceGridSection
import com.hogumiwarts.lumos.ui.common.DeviceRoutineCard
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.Routine.components.GlowingCard
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun RoutineDeviceListScreen(
    viewModel: RoutineDeviceListViewModel = hiltViewModel(),
    devices: List<MyDevice>,
    onSelectComplete: (MyDevice) -> Unit,
    showDuplicateDialog: Boolean,
    onDismissDuplicateDialog: () -> Unit
) {
    // 선택 기기 상태
    val selectedDeviceId by viewModel.selectedDeviceId
    val showDialog by viewModel.showDialog

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .statusBarsPadding(),
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // topBar
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "나의 기기 목록",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = nanum_square_neo
            )

        }


        // 기기 목록
        DeviceGridSection(
            devices = devices,
            selectedDeviceId = selectedDeviceId,
            onDeviceClick = { viewModel.onDeviceClicked(it) }
        )

        Spacer(modifier = Modifier.height(125.dp))

        // 선택 버튼
        PrimaryButton(
            buttonText = "선택하기",
            onClick = {
                val selected = viewModel.getSelectedDevice(devices)
                if (selected != null) {
                    onSelectComplete(selected)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // 다이얼로그 설정
        CommonDialog(
            showDialog = showDialog,
            onDismiss = { viewModel.dismissDialog() },
            titleText = "선택할 수 없는 기기예요!",
            bodyText = "기기 상태가 비활성화로 감지되어 제어할 수 없습니다. 거리가 멀어지면 비활성화로 전환될 수 있어요."
        )

        // 중복 기기용 다이얼로그
        CommonDialog(
            showDialog = showDuplicateDialog,
            onDismiss = onDismissDuplicateDialog,
            titleText = "이미 선택한 기기예요!",
            bodyText = "같은 기기 + 같은 상태 조합은 한 번만 사용할 수 있어요. 새로운 조합으로 시도해볼까요? ✨"
        )
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun RoutineDeviceListScreenPreview() {
//    RoutineDeviceListScreen(
//        devices = MyDevice.sample,
//        onSelectComplete = {},
//    )
//}