package com.hogumiwarts.lumos.ui.screens.routine.routineEdit

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconList
import com.hogumiwarts.lumos.ui.screens.routine.components.SwipeableDeviceCard
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListViewModel
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditScreen(
    viewModel: RoutineEditViewModel,
    devices: List<CommandDevice>,
    onRoutineEditComplete: () -> Unit,
    navController: NavController
) {
    val selectedIcon by viewModel.selectedIcon.collectAsState()
    val routineName by viewModel.routineName.collectAsState()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // 바텀 시트
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpen by remember { mutableStateOf(false) }

    // 기기 리스트 관리
    val deviceList = remember { mutableStateListOf<CommandDevice>().apply { addAll(devices) } }
    val myDeviceList = remember {
        mutableStateListOf<MyDevice>().apply {
            addAll(MyDevice.sample.map {
                MyDevice(
                    deviceId = it.deviceId,
                    deviceName = it.deviceName,
                    isOn = it.isOn,
                    isActive = it.isActive,
                    deviceType = it.deviceType
                )
            })
        }
    }
    val showDuplicateDialog = remember { mutableStateOf(false) }

    if (isSheetOpen) {
        val deviceListViewModel: RoutineDeviceListViewModel = hiltViewModel()

        ModalBottomSheet(
            onDismissRequest = { isSheetOpen = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            RoutineDeviceListScreen(
                viewModel = deviceListViewModel,
                onSelectComplete = { selectedDevice ->
                    // 이미 등록된 기기 중 중복 검사
                    val isDuplicate = deviceList.any { it.deviceId == selectedDevice.deviceId }
                    if (isDuplicate) {
                        showDuplicateDialog.value = true
                    } else {
                        // 기기 추가
//                        deviceList.add(/* 변환 후 추가 */)
//                        isSheetOpen = false
                    }
                },
                showDuplicateDialog = showDuplicateDialog,
                onDismissDuplicateDialog = { showDuplicateDialog.value = false },
                navController = navController,
                alreadyAddedDeviceIds = deviceList.map { it.deviceId }
            )

        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 28.dp)
            .statusBarsPadding(),
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f),
            contentPadding = PaddingValues(top = 25.dp, bottom = 50.dp),
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
                        fontSize = 18.sp,
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

            item { Box(modifier = Modifier.height(1.dp)) {} }

            // 루틴 이름
            item {
                // 제목
                Text(
                    text = "루틴 이름",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF000000),
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 루틴 이름 입력창
                OutlinedTextField(
                    value = routineName,
                    onValueChange = { viewModel.onRoutineNameChanged(it) },
                    isError = state.nameBlankMessage != null,
                    placeholder = {
                        Text(
                            "이름을 설정해주세요.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                fontFamily = nanum_square_neo,
                                fontWeight = FontWeight(400),
                                color = Color(0xFFBEBEBE),
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(51.dp)
                        .then( // 이름 입력 안했으면 빨간색으로 강조하여 알림
                            if (state.nameBlankMessage != null) Modifier
                                .border(
                                    1.5.dp,
                                    Color(0xFFF26D6D),
                                    shape = MaterialTheme.shapes.medium
                                )
                            else Modifier
                        ),
                    singleLine = true,
                    shape = MaterialTheme.shapes.medium,
                    trailingIcon = {
                        if (routineName.isNotEmpty()) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_cancel),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { viewModel.onRoutineNameChanged("") }
                            )
                        }
                    }
                )
            }

            item { Box(modifier = Modifier.height(1.dp)) {} }


            // 적용 기기
            item {
                Row(

                ) {
                    // 제목
                    Text(
                        "적용 기기",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            lineHeight = 16.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(800),
                            color = Color(0xFF000000),
                        )
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                //기기 추가 화면으로 이동
                                //navController.navigate("routineDeviceList")
//                               isSheetOpen = true
                                coroutineScope.launch {
                                    isSheetOpen = true
                                    sheetState.show() // 바텀 시트 열기
                                }
                            }
                    ) {
                        Image(
                            painterResource(id = R.drawable.ic_plus),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "기기 추가",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                fontFamily = nanum_square_neo,
                                fontWeight = FontWeight(700),
                                color = Color(0xFFBFC2D7),
                            )
                        )
                    }
                }
            }

            // 기기 리스트
            items(deviceList, key = { it.deviceId }) { device ->
                var visible by remember { mutableStateOf(true) }
                if (visible) {
                    var shouldRemove by remember { mutableStateOf(false) }
                    if (shouldRemove) {
                        LaunchedEffect(device) {
                            delay(300)
                            deviceList.remove(device)
                        }
                    }
                    AnimatedVisibility(
                        visible = !shouldRemove,
                        exit = shrinkVertically(tween(300)) + fadeOut()
                    ) {
                        SwipeableDeviceCard(device = device, onDelete = {
                            shouldRemove = true
                            Toast.makeText(
                                context,
                                "${device.deviceName.appendSubject()} 삭제되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                            onClick = {
                                // 기기 카드 클릭 시 행동 정의 (예: 제어화면 이동)
                                val json = Gson().toJson(device)
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    "commandDeviceJson",
                                    json
                                )
                                navController.navigate("routineDeviceList")
                            }
                        )
                    }
                }
            }

            // 제스처 선택
            // 제목
            item {
                Text(
                    "제스처 선택",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF000000),
                    )
                )
            }

            // 제스처 카드
//            item {
//                GestureCard(selectedGesture = GestureType.DOUBLE_CLAP, isEditMode = true)
//            }


            item {
                Spacer(modifier = Modifier.height(50.dp))
            }

        }


        // 수정 버튼
        Box(
            modifier = Modifier
                .align(
                    Alignment.CenterHorizontally
                )
                .padding(bottom = 40.dp, top = 20.dp)
                .clickable {
                    //todo: 수정 api 연동
                    onRoutineEditComplete()
                }
        ) {
            PrimaryButton(buttonText = "수정하기", onClick = {}, modifier = Modifier.fillMaxWidth())
        }
    }


}

fun String.appendSubject(): String {
    val lastChar = this.last()
    val hasJong = (lastChar.code - 0xAC00) % 28 != 0
    return if (hasJong) "${this}이" else "${this}가"
}


//@Composable
//fun SelectIcon() {
//    TODO("Not yet implemented")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun RoutineEditScreenPreview() {
//    val fakeViewModel = remember { RoutineEditViewModel() }
//
//    RoutineEditScreen(
//        viewModel = fakeViewModel,
//        devices = RoutineDevice.sample,
//        onRoutineEditComplete = {},
//        navController = rememberNavController()
//    )
//}