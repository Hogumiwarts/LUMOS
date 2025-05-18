package com.hogumiwarts.lumos.ui.screens.routine.routineCreate


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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toCommandDevice
import com.hogumiwarts.lumos.mapper.toCommandDeviceForAirPurifier
import com.hogumiwarts.lumos.mapper.toCommandDeviceForSpeaker
import com.hogumiwarts.lumos.mapper.toCommandDeviceForSwitch
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.screens.routine.components.GestureCard
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconList
import com.hogumiwarts.lumos.ui.screens.routine.components.SwipeableDeviceCard
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListViewModel
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineCreateScreen(
    viewModel: RoutineCreateViewModel,
    onRoutineCreateComplete: () -> Unit,
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
    val devices by viewModel.devices.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    val selectedGesture by viewModel.selectedGesture.collectAsState()

    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<GestureData>("selectedGesture")
            ?.observe(lifecycleOwner) { gestureData ->
                viewModel.setGestureData(gestureData)
            }

    }


    val myDeviceList = remember {
        mutableStateListOf<MyDevice>().apply {
            addAll(MyDevice.sample)
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
                devices = deviceListViewModel.devices.value,
                onSelectComplete = { selectedDevice ->
                    val commandDevice = when (selectedDevice.deviceType) {
                        DeviceListType.LIGHT -> selectedDevice.toCommandDevice(
                            isOn = true,
                            brightness = 50,
                            hue = null,
                            saturation = null
                        )

                        DeviceListType.AIRPURIFIER -> {
                            val json =
                                navController.previousBackStackEntry?.savedStateHandle?.get<String>(
                                    "commandDeviceJson"
                                )
                            json?.let {
                                Gson().fromJson(it, CommandDevice::class.java)
                            } ?: selectedDevice.toCommandDeviceForAirPurifier(
                                isOn = false,
                                fanMode = "auto"
                            )
                        }

                        DeviceListType.AUDIO -> {
                            val json = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<String>("commandDeviceJson")
                            json?.let {
                                Gson().fromJson(it, CommandDevice::class.java)
                            } ?: selectedDevice.toCommandDeviceForSpeaker(
                                isOn = true,
                                volume = 30,
                                isPlaying = true
                            )
                        }

                        DeviceListType.SWITCH -> {
                            val json = navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.get<String>("commandDeviceJson")
                            json?.let {
                                Gson().fromJson(it, CommandDevice::class.java)
                            } ?: selectedDevice.toCommandDeviceForSwitch(
                                isOn = true
                            )
                        }

                        DeviceListType.ETC -> TODO()
                    }

                    if (devices.any { it.deviceId == commandDevice.deviceId }) {
                        showDuplicateDialog.value = true
                    } else {
                        viewModel.addDevice(commandDevice)
                        isSheetOpen = false
                    }

                },
                showDuplicateDialog = showDuplicateDialog.value,
                onDismissDuplicateDialog = { showDuplicateDialog.value = false },
                navController = navController
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
                        text = "루틴 생성",
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
                    onValueChange = {
                        viewModel.onRoutineNameChanged(it)
                        if (state.nameBlankMessage != null) {
                            viewModel.clearNameError()
                        }
                    },
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

                if (state.nameBlankMessage != null) {
                    Text(
                        text = state.nameBlankMessage ?: "",
                        color = Color(0xFFF26D6D),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                        fontFamily = nanum_square_neo
                    )
                }
            }

            item { Box(modifier = Modifier.height(1.dp)) {} }


            // 적용 기기
            item {
                Row {
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
                                coroutineScope.launch {
                                    isSheetOpen = true
                                    sheetState.show() // 바텀 시트 열기
                                }
                            }
                    ) {
                        if (devices.isNotEmpty()) {
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
            }

            if (devices.isEmpty()) {
                // 비어있으면 하단에 기기 추가 버튼 생성
                item {
                    AddDeviceCard(
                        onClick = {
                            coroutineScope.launch {
                                isSheetOpen = true
                                sheetState.show()
                            }
                        },
                        text = "기기 추가"
                    )

                    if (state.deviceEmptyMessage != null) {
                        Text(
                            text = state.deviceEmptyMessage!!,
                            color = Color(0xFFF26D6D),
                            fontSize = 12.sp,
                            fontFamily = nanum_square_neo,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }
                }
            }


            // 기기 리스트
            items(devices, key = { it.deviceId }) { device ->
                var shouldRemove by remember(device.deviceId) { mutableStateOf(false) }

                if (!shouldRemove) {
                    AnimatedVisibility(
                        visible = true,
                        exit = shrinkVertically(tween(300)) + fadeOut()
                    ) {
                        SwipeableDeviceCard(
                            device = device,
                            onDelete = {
                                shouldRemove = true
                                coroutineScope.launch {
                                    delay(300)
                                    viewModel.deleteDevice(device)
                                }
                            }
                        )
                    }
                }
            }

            item { Box(modifier = Modifier.height(1.dp)) {} }

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

            item {
                if (selectedGesture != null) {
                    GestureCard(
                        selectedGesture = selectedGesture!!,
                        isEditMode = true,
                        onChangeGestureClick = { navController.navigate("gesture_select") }
                    )
                } else {
                    AddDeviceCard(
                        onClick = { navController.navigate("gesture_select") },
                        text = "제스처 추가"
                    )

                    if (state.deviceEmptyMessage != null) {
                        Text(
                            text = state.deviceEmptyMessage!!,
                            color = Color(0xFFF26D6D),
                            fontSize = 12.sp,
                            fontFamily = nanum_square_neo,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }
                }
            }


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
        ) {
            PrimaryButton(
                buttonText = "생성하기",
                onClick = {
                    viewModel.createRoutine(
                        onSuccess = {
                            Timber.tag("Routine").d("🟢 루틴 생성 요청됨")
                            onRoutineCreateComplete()
                        },
                        onError = { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                            Timber.tag("routine").e("❌ 루틴 생성 중 오류: $errorMessage")
                            onRoutineCreateComplete()
                        }
                    )

                },
                modifier = Modifier.fillMaxWidth()
            )

        }
    }


}


@Composable
fun AddDeviceCard(onClick: () -> Unit, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = Color(0xFFD9DCE8),
                shape = RoundedCornerShape(size = 10.dp)
            )
            .background(color = Color(0xFFF5F6F9), shape = RoundedCornerShape(size = 10.dp)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(id = R.drawable.ic_plus),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = text,
            style = TextStyle(
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight(700),
                color = Color(0xFFBFC2D7),

                )
        )
    }
}

fun String.appendSubject(): String {
    val lastChar = this.last()
    val hasJong = (lastChar.code - 0xAC00) % 28 != 0
    return if (hasJong) "${this}이" else "${this}가"
}


@Composable
fun SelectIcon() {
    TODO("Not yet implemented")
}

//@Preview(showBackground = true, widthDp = 360, heightDp = 800)
//@Composable
//fun RoutineCreateScreenPreview() {
//    val fakeViewModel = remember { RoutineCreateViewModel() }
//
//    RoutineCreateScreen(
//        viewModel = fakeViewModel,
//        onRoutineCreateComplete = {},
//        navController = rememberNavController()
//    )
//}
