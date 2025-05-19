package com.hogumiwarts.lumos.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.gesture.GestureScreen
import com.hogumiwarts.lumos.ui.screens.devices.DeviceListScreen
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel
import com.hogumiwarts.lumos.ui.screens.home.HomeScreen
import com.hogumiwarts.lumos.ui.screens.setting.SettingScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineCreate.RoutineCreateScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineCreate.RoutineCreateViewModel
import com.hogumiwarts.lumos.ui.screens.routine.routineDetail.RoutineDetailScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineDetail.RoutineDetailViewModel
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.RoutineDeviceListViewModel
import com.hogumiwarts.lumos.ui.screens.routine.routineEdit.RoutineEditScreen
import com.hogumiwarts.lumos.ui.screens.routine.routineEdit.RoutineEditViewModel
import com.hogumiwarts.lumos.ui.screens.routine.routineList.RoutineScreen
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginScreen
import com.hogumiwarts.lumos.ui.screens.auth.onboarding.WelcomeScreen
import com.hogumiwarts.lumos.ui.screens.auth.signup.SignupScreen
import com.hogumiwarts.lumos.ui.screens.control.AirpurifierScreen
import com.hogumiwarts.lumos.ui.screens.control.DetectDeviceScreen
import com.hogumiwarts.lumos.ui.screens.control.audio.SpeakerScreen
import com.hogumiwarts.lumos.ui.screens.control.SwitchScreen
import com.hogumiwarts.lumos.ui.screens.control.light.LightScreen
import com.hogumiwarts.lumos.ui.screens.control.light.RealLightScreenContent
import com.hogumiwarts.lumos.ui.screens.control.airpurifier.PreviewAirPurifierScreenContent
import com.hogumiwarts.lumos.ui.screens.routine.components.RoutineIconType
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.devicecontrolscreen.PreviewSpeakerScreenContent
import com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.devicecontrolscreen.PreviewSwitchScreenContent

@Composable
fun NavGraph(
    deviceId: Long, deviceType: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // 화면 순서 정의 (바텀 네비게이션 순서와 일치)
    val screens = listOf(
        BottomNavItem.Home,
        BottomNavItem.Info,
        BottomNavItem.Routine,
        BottomNavItem.Settings,
    )

    // 화면 경로만 추출
    val screenOrder = screens.map { it.route }

    val viewModel: AuthViewModel = hiltViewModel()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    val tokenDataStore = TokenDataStore(context = LocalContext.current)

    if (isLoggedIn != null) {
        val startDestination = if (isLoggedIn == true) {
            if (deviceId == -1L || deviceType == "") {
                "home"
            } else {
                deviceType
            }

        } else "welcome"
        //val startDestination = "welcome"

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {

            // 시작 화면
            composable("welcome") {
                WelcomeScreen(
                    onStartClick = { navController.navigate("signup") },
                    onLoginClick = { navController.navigate("login") }
                )
            }

            // 각 화면에 대한 composable 설정
            screens.forEach { item ->
                composable(
                    route = item.route,
                    enterTransition = {
                        val fromRoute = initialState.destination.route
                        val toRoute = targetState.destination.route

                        // 현재 화면이 왼쪽, 새 화면이 오른쪽일 때 (왼→오 이동)
                        if (fromRoute != null && toRoute != null && getNavigationDirection(
                                fromRoute,
                                toRoute,
                                screenOrder
                            )
                        ) {
                            // 오른쪽에서 들어옴
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                animationSpec = tween(300)
                            )
                        } else {
                            // 왼쪽에서 들어옴
                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                animationSpec = tween(300)
                            )
                        }
                    },
                    exitTransition = {
                        val toRoute = targetState.destination.route

                        // findDeviceScreen으로 이동할 때
                        // 현재 화면이 위로 올라가는 애니메이션
                        if (toRoute == "findDeviceScreen") {

                            slideOutOfContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(200)
                            )

                        } else {
                            val fromRoute = initialState.destination.route

                            if (fromRoute != null && toRoute != null && getNavigationDirection(
                                    fromRoute,
                                    toRoute,
                                    screenOrder
                                )
                            ) {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                )
                            } else {
                                slideOutOfContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300)
                                )
                            }
                        }
                    },
                    // 뒤로가기로 진입 시 애니메이션 (ControlScreen에서 돌아올 때)
                    popEnterTransition = {
                        val fromRoute = initialState.destination.route

                        // ControlScreen에서 돌아올 때
                        if (fromRoute == "findDeviceScreen") {

                            slideIntoContainer(
                                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                                animationSpec = tween(200)
                            )

                        } else {
                            val toRoute = targetState.destination.route

                            if (fromRoute != null && toRoute != null && getNavigationDirection(
                                    fromRoute,
                                    toRoute,
                                    screenOrder
                                )
                            ) {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                                    animationSpec = tween(300)
                                )
                            } else {
                                slideIntoContainer(
                                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                                    animationSpec = tween(300)
                                )
                            }
                        }
                    }
                ) {
                    // 각 화면에 맞는 Composable 함수 호출
                    when (item) {
                        BottomNavItem.Home -> {
                            HomeScreen(tokenDataStore = tokenDataStore, navController = navController)
//                            LightScreen()
//                            GestureScreen()
                        }

                        BottomNavItem.Info -> {
                            val myDeviceList = MyDevice.sample

                            DeviceListScreen(
                                navController = navController
                            )
                        }

                        BottomNavItem.Routine -> RoutineScreen(
                            onRoutineClick = { routine ->
                                navController.navigate("routine_detail/${routine.routineId}")
                            },
                            onAddClick = {
                                navController.navigate("routine_create")
                            },
                        )


                        BottomNavItem.Settings -> SettingScreen(
                            authViewModel = viewModel,
                            navController = navController
                        )
                    }
                }
            }

            composable(
                route = "findDeviceScreen",
                // ControlScreen 진입 시 - 아래에서 위로 올라옴
                enterTransition = {
                    // 아래에서 위로 올라오는 애니메이션
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(200)
                    )
                },
                // ControlScreen 이탈 시
                exitTransition = {
                    // 위에서 아래로 내려가는 애니메이션
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(200)
                    )
                },
            ) {
//                FindDeviceScreen(navController = navController)
                DetectDeviceScreen(navController = navController)
            }

            // Auth
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
            composable("signup") {
                SignupScreen(
                    onSignupSuccess = {
                        navController.navigate("login") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                )
            }

            // 루틴 상세
            composable(
                "routine_detail/{routineId}",
                enterTransition = { fadeIn(tween(300)) },
                popExitTransition = { fadeOut(tween(300)) }
            ) { backStackEntry ->
                val routineId = backStackEntry.arguments?.getString("routineId")
                val viewModel = hiltViewModel<RoutineDetailViewModel>()

                RoutineDetailScreen(
                    routineId = routineId,
                    viewModel = viewModel,
                    navController = navController,
                    onEdit = {
                        navController.navigate("routine_edit/$routineId")
                    }
                )

            }

            // 루틴 수정
            composable("routine_edit/{routineId}") { navBackStackEntry ->
                val routineId = navBackStackEntry.arguments?.getLong("routineId")
                val viewModel = hiltViewModel<RoutineEditViewModel>()
                val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
                val editGestureData = savedStateHandle?.get<GestureData>("selectedGesture")

                val editRoutineName = savedStateHandle?.get<String>("editRoutineName") ?: ""
                val editRoutineIcon = savedStateHandle?.get<String>("editRoutineIcon")
                val editDevices =
                    savedStateHandle?.get<List<CommandDevice>>("editDevices") ?: emptyList()

                // 초기 상태 로드
                LaunchedEffect(Unit) {
                    val editGestureData = savedStateHandle?.get<GestureData>("selectedGesture")
                    editGestureData?.let {
                        viewModel.setGestureData(it)
                        savedStateHandle.remove<GestureData>("selectedGesture")
                    }

                    viewModel.onRoutineNameChanged(editRoutineName)
                    editRoutineIcon?.let { iconName ->
                        RoutineIconType.entries.find { it.name == iconName }?.let {
                            viewModel.selectIcon(it)
                        }
                    }
                    viewModel.loadInitialDevices(editDevices)
                    viewModel.setRoutineId(routineId)
                }

                RoutineEditScreen(
                    viewModel = viewModel,
                    onRoutineEditComplete = {
                        navController.popBackStack()
                    },
                    navController = navController
                )
            }


            // 루틴 - 기기 선택
            composable("routineDeviceList") {
                val viewModel = hiltViewModel<RoutineDeviceListViewModel>()
                val showDuplicateDialog = remember { mutableStateOf(false) }

                RoutineDeviceListScreen(
                    viewModel = viewModel,
                    onSelectComplete = {
                        navController.popBackStack()
                    },
                    showDuplicateDialog = showDuplicateDialog,
                    onDismissDuplicateDialog = { showDuplicateDialog.value = false },
                    navController = navController,
                    alreadyAddedDeviceIds = listOf()
                )
            }


            composable("light_control?preview={preview}", arguments = listOf(
                navArgument("preview") { defaultValue = "false" }
            )) {
                val preview = it.arguments?.getString("preview")?.toBoolean() ?: false
                val selectedDevice = navController.previousBackStackEntry
                    ?.savedStateHandle?.get<MyDevice>("selectedDevice")

                selectedDevice?.let {
                    LightScreen(
                        selectedDevice = it,
                        previewMode = preview,
                        navController = navController
                    )
                }

            }

            // 워치에서 호출 후 조명 제어화면
            composable("LIGHT") {
                RealLightScreenContent(
                    deviceId = deviceId
                )
            }
            // 워치에서 호출 후 공기 청정기 제어화면
            composable("AIRPURIFIER") {

                AirpurifierScreen(
                    deviceId = deviceId
                )
            }
            // 워치에서 호출 후 공기 청정기 제어화면
            composable("AUDIO") {
                SpeakerScreen(
                    deviceId = deviceId
                )
            }
            // 워치에서 호출 후 공기 청정기 제어화면
            composable("SWITCH") {
                SwitchScreen(
                    deviceId = deviceId
                )
            }

            // 모바일에서 호출

            composable("LIGHT/{deviceId}") {
                val deviceId = it.arguments?.getString("deviceId")?.toLong() ?: -1L
                RealLightScreenContent(
                    deviceId = deviceId
                )
            }

            composable("AIRPURIFIER/{deviceId}") {
                val deviceId = it.arguments?.getString("deviceId")?.toLong() ?: -1L
                AirpurifierScreen(
                    deviceId = deviceId
                )
            }

            composable("AUDIO/{deviceId}") {
                val deviceId = it.arguments?.getString("deviceId")?.toLong() ?: -1L
                SpeakerScreen(
                    deviceId = deviceId
                )
            }

            composable("SWITCH/{deviceId}") {
                val deviceId = it.arguments?.getString("deviceId")?.toLong() ?: -1L
                SwitchScreen(
                    deviceId = deviceId
                )
            }


            composable("routine_create") {
                val viewModel = hiltViewModel<RoutineCreateViewModel>()
                RoutineCreateScreen(
                    viewModel = viewModel,
                    onRoutineCreateComplete = {
                        navController.popBackStack()
                    },
                    navController = navController
                )
            }

            composable("gesture_select") {
                GestureScreen(
                    navController = navController,
                    onGestureSelected = { gestureId ->
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selectedGestureId", gestureId)
                        navController.popBackStack()
                    }
                )
            }

            composable("airpurifier_control?preview={preview}") {
                val preview = it.arguments?.getString("preview")?.toBoolean() ?: false
                val selectedDevice = navController.previousBackStackEntry
                    ?.savedStateHandle?.get<MyDevice>("selectedDevice")

                selectedDevice?.let {
                    if (preview) {
                        PreviewAirPurifierScreenContent(
                            navController = navController,
                            selectedDevice = it
                        )
                    } else {
                        AirpurifierScreen(deviceId = it.deviceId.toLong())
                    }
                }
            }

            composable("switch_control?preview={preview}") {
                val preview = it.arguments?.getString("preview")?.toBoolean() ?: false
                val selectedDevice = navController.previousBackStackEntry
                    ?.savedStateHandle?.get<MyDevice>("selectedDevice")

                selectedDevice?.let {
                    if (preview) {
                        PreviewSwitchScreenContent(
                            navController = navController,
                            selectedDevice = it
                        )
                    } else {
                        SwitchScreen(it.deviceId.toLong())
                    }
                }
            }

            composable("speaker_control?preview={preview}") {
                val preview = it.arguments?.getString("preview")?.toBoolean() ?: false
                val selectedDevice = navController.previousBackStackEntry
                    ?.savedStateHandle?.get<MyDevice>("selectedDevice")

                selectedDevice?.let {
                    if (preview) {
                        PreviewSpeakerScreenContent(
                            navController = navController,
                            selectedDevice = it
                        )
                    } else {
                        SpeakerScreen(it.deviceId.toLong()) // 실제 제어 화면
                    }
                }
            }


        }
    } else {

    }


}