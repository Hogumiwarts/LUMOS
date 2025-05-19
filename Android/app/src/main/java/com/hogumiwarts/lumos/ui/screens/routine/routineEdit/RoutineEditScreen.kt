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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.DataStore.TokenDataStore
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
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconType
import com.hogumiwarts.lumos.ui.screens.routine.components.SwipeableDeviceCard
import com.hogumiwarts.lumos.ui.screens.routine.routineCreate.AddDeviceCard
import com.hogumiwarts.lumos.ui.screens.routine.routineCreate.SwipeableDeviceCardWithHint
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListViewModel
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditScreen(
    viewModel: RoutineEditViewModel,
    onRoutineEditComplete: () -> Unit,
    navController: NavController,
) {
    val selectedIcon by viewModel.selectedIcon.collectAsState()
    val routineName by viewModel.routineName.collectAsState()
    val deviceList by viewModel.devices.collectAsState()
    val selectedGesture by viewModel.selectedGesture.collectAsState()
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val tokenDataStore = TokenDataStore(context)

    // 바텀 시트
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    var isSheetOpen by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(navController.currentBackStackEntry) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<GestureData>("selectedGesture")
            ?.observe(lifecycleOwner) { gestureData ->
                viewModel.setGestureData(gestureData)
            }

        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<String>("commandDeviceJson")
            ?.observe(lifecycleOwner) { json ->
                val updatedDevice = Gson().fromJson(json, CommandDevice::class.java)
                viewModel.updateDevice(updatedDevice)

                navController.previousBackStackEntry?.savedStateHandle?.remove<String>("commandDeviceJson")
            }

    }

    var initialized by rememberSaveable { mutableStateOf(false) }

    if (!initialized) {
        val devices = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<List<CommandDevice>>("editDevices")

        viewModel.loadInitialDevicesOnce(devices ?: emptyList())

        initialized = true
    }

    // 초기 데이터 설정
    LaunchedEffect(Unit) {
        val routineId = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<Long>("editRoutineId")

        val routineName = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("editRoutineName")

        val routineIcon = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<String>("editRoutineIcon")

        val gestureData = navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<GestureData>("selectedGesture")

        routineId?.let { viewModel.setRoutineId(it) }
        gestureData?.let { viewModel.setGestureData(it) }
        routineName?.let { viewModel.onRoutineNameChanged(it) }

        // 아이콘 문자열을 Enum으로 변환
        routineIcon?.let { iconName ->
            RoutineIconType.entries.find { it.iconName == iconName || it.name == iconName }?.let {
                viewModel.selectIcon(it)
            }
        }
    }

    // 기기 리스트 관리
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
                    val json = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<String>("commandDeviceJson")

                    val commandDevice = when (selectedDevice.deviceType) {
                        DeviceListType.LIGHT -> {
                            json?.let {
                                Gson().fromJson(it, CommandDevice::class.java)
                            } ?: selectedDevice.toCommandDevice(
                                isOn = true,
                                brightness = 50,
                                hue = null,
                                saturation = null
                            )
                        }

                        DeviceListType.AIRPURIFIER -> {
                            json?.let {
                                Gson().fromJson(it, CommandDevice::class.java)
                            } ?: selectedDevice.toCommandDeviceForAirPurifier(
                                isOn = false,
                                fanMode = "auto"
                            )
                        }

                        DeviceListType.AUDIO -> {
                            json?.let {
                                Gson().fromJson(it, CommandDevice::class.java)
                            } ?: selectedDevice.toCommandDeviceForSpeaker(
                                isOn = true,
                                volume = 30,
                                isPlaying = true
                            )
                        }

                        DeviceListType.SWITCH -> {
                            json?.let {
                                Gson().fromJson(it, CommandDevice::class.java)
                            } ?: selectedDevice.toCommandDeviceForSwitch(
                                isOn = true
                            )
                        }

                        DeviceListType.ETC -> TODO()
                    }

                    val isDuplicate = deviceList.any { it.deviceId == commandDevice.deviceId }
                    if (isDuplicate) {
                        showDuplicateDialog.value = true
                    } else {
                        viewModel.addDevice(commandDevice)

                        // 추가 후 commandDeviceJson 초기화
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.remove<String>("commandDeviceJson")

                        isSheetOpen = false
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "적용 기기",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight(800),
                            fontFamily = nanum_square_neo
                        )
                    )

                    Spacer(Modifier.weight(1f))

                    if (deviceList.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                coroutineScope.launch { isSheetOpen = true; sheetState.show() }
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_plus),
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                "기기 추가",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight(700),
                                    color = Color(0xFFBFC2D7)
                                )
                            )
                        }
                    }
                }
            }

            if (deviceList.isEmpty()) {
                item {
                    Column {
                        AddDeviceCard(
                            onClick = {
                                coroutineScope.launch { isSheetOpen = true; sheetState.show() }
                            },
                            text = "기기 추가"
                        )
                        state.deviceEmptyMessage?.let {
                            Text(
                                text = it,
                                color = Color(0xFFF26D6D),
                                fontSize = 12.sp,
                                fontFamily = nanum_square_neo,
                                modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                            )
                        }
                    }
                }
            }

            // 기기 리스트
            items(deviceList, key = {
                "${it.deviceId}_${it.hashCode()}"
            }) { device ->
                var shouldRemove by remember(device.deviceId) { mutableStateOf(false) }
                var shouldShowHint by remember(device.deviceId) { mutableStateOf(true) }
                AnimatedVisibility(
                    visible = !shouldRemove,
                    exit = shrinkVertically(tween(300)) + fadeOut(tween(300))
                ) {
                    SwipeableDeviceCardWithHint(
                        deviceId = device.deviceId,
                        shouldShowHint = shouldShowHint,
                        onHintShown = { shouldShowHint = false },
                        onDelete = {
                            shouldRemove = true
                            coroutineScope.launch {
                                delay(300)
                                viewModel.deleteDevice(device)
                            }
                        },
                        deviceContent = {
                            SwipeableDeviceCard(
                                device = device,
                                onDelete = {
                                    shouldRemove = true
                                    coroutineScope.launch {
                                        delay(300)
                                        viewModel.deleteDevice(device)
                                    }
                                },
                                onClick = {
                                    val myDevice = MyDevice(
                                        deviceId = device.deviceId,
                                        deviceName = device.deviceName,
                                        isOn = device.commands.any { it.capability == "switch" && it.command == "on" },
                                        isActive = true, // 편의상 true로 지정 (비활성 구분 안 할 경우)
                                        deviceType = DeviceListType.from(device.deviceType),
                                        commands = device.commands
                                    )

                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        "selectedDevice",
                                        myDevice
                                    )

                                    when (DeviceListType.from(device.deviceType)) {
                                        DeviceListType.LIGHT -> navController.navigate("light_control?preview=true")
                                        DeviceListType.SWITCH -> navController.navigate("switch_control?preview=true")
                                        DeviceListType.AIRPURIFIER -> navController.navigate("airpurifier_control?preview=true")
                                        DeviceListType.AUDIO -> navController.navigate("speaker_control?preview=true")
                                        else -> Toast.makeText(
                                            context,
                                            "지원하지 않는 기기 타입이에요!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            )
                        }
                    )
                }
            }
            // 제스처 선택
            // 제목
            item {
                Text(
                    "제스처 선택", style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = nanum_square_neo
                    )
                )
            }

            item {
                if (selectedGesture != null) {
                    GestureCard(
                        selectedGesture = selectedGesture!!,
                        isEditMode = true,
                        onChangeGestureClick = {
                            navController.navigate("gesture_select")
                        }
                    )
                } else {
                    AddDeviceCard(
                        onClick = { navController.navigate("gesture_select") },
                        text = "제스처 추가"
                    )
                }
            }



            item {
                Spacer(modifier = Modifier.height(50.dp))
            }

        }

        // 수정 버튼
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 40.dp, top = 20.dp)
        ) {
            PrimaryButton(
                buttonText = "수정하기",
                onClick = {
                    coroutineScope.launch {
                        val accessToken = tokenDataStore.getAccessToken().first()
                        viewModel.updateRoutine(
                            routineId = viewModel.routineId.value,
                            accessToken = accessToken,
                            gestureId = viewModel.gestureId.value
                        )
                        onRoutineEditComplete()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }


}

fun String.appendSubject(): String {
    val lastChar = this.last()
    val hasJong = (lastChar.code - 0xAC00) % 28 != 0
    return if (hasJong) "${this}이" else "${this}가"
}


@Composable
fun AddDeviceCard(onClick: () -> Unit, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable { onClick() }
            .border(1.dp, Color(0xFFD9DCE8), RoundedCornerShape(10.dp))
            .background(Color(0xFFF5F6F9), RoundedCornerShape(10.dp)),
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
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFBFC2D7),
            fontFamily = nanum_square_neo
        )
    }

}