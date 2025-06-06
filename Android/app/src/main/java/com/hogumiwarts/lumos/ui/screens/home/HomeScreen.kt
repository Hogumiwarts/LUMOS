package com.hogumiwarts.lumos.ui.screens.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.hogumiwarts.domain.model.WeatherInfo
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.CommonDialog
import com.hogumiwarts.lumos.ui.common.DeviceGridHomeSection
import com.hogumiwarts.lumos.ui.common.DeviceGridSection
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.SkeletonComponent
import com.hogumiwarts.lumos.ui.screens.control.ControlViewModel
import com.hogumiwarts.lumos.ui.screens.devices.DeviceListViewModel
import com.hogumiwarts.lumos.ui.screens.devices.NotLinkedScreen
import com.hogumiwarts.lumos.ui.screens.home.components.LightDeviceItem
import com.hogumiwarts.lumos.ui.screens.home.components.WeatherCardView
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel
import com.hogumiwarts.lumos.utils.CommonUtils
import com.hogumiwarts.lumos.utils.getCurrentLocation
import org.orbitmvi.orbit.compose.collectAsState
import timber.log.Timber

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    deviceViewModel: DeviceListViewModel = hiltViewModel(),
    controlViewModel: ControlViewModel = hiltViewModel(),

    authViewModel: AuthViewModel = hiltViewModel(),
    tokenDataStore: TokenDataStore,
    navController: NavController
) {
    val context = LocalContext.current
    val weatherState by homeViewModel.collectAsState()
    val isWeatherLoading = weatherState.isLoading

    val showDialog by deviceViewModel.showDialog

    val isLinked by deviceViewModel.isLinked.collectAsState()
    val deviceList by deviceViewModel.deviceList.collectAsState()
    val filteredDevices = deviceList
        .filter { it.deviceType != DeviceListType.ETC }
        .sortedBy { device ->
            if (device.deviceType == DeviceListType.AUDIO) 1 else 0
        }

    val HomeState by homeViewModel.collectAsState()

    val clickDevice by deviceViewModel.clickDevice

    LaunchedEffect(Unit) {
        Log.d("TAG", "HomeScreen: 호출")

        deviceViewModel.getJwt()
        controlViewModel.prepareSession()
        deviceViewModel.checkAccountLinked()


//        authViewModel.refreshToken()
        val location = getCurrentLocation(context)

        if (location != null) {
            Timber.tag("HomeScreen").d("lat: ${location.latitude}, ${location.longitude}")
            homeViewModel.onIntent(
                HomeIntent.LoadWeather(
                    latitude = location.latitude, longitude = location.longitude
                )
            )
        } else {
            Toast.makeText(context, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        // 멤버 정보 호출
        homeViewModel.getMemberInfo()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(PaddingValues(0.dp))
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF202E70), // Top
                            Color(0xFF394587),
                            Color(0xFF4A5597),
                            Color(0xFF5661A2),
                            Color(0xFF606BAC),
                            Color(0xFF717BBC)  // Bottom
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 120.dp)
                .padding(horizontal = 28.dp)
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Column() {
                Text(
                    text = CommonUtils.getFormattedToday(),
                    fontSize = 14.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )

                Text(
                    text = "${controlViewModel.localAddress}",
                    fontSize = 9.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA1A1A1),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                )

            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = "${HomeState.userName ?: "루모스"}님\n집에 돌아오신 걸 환영해요. ",
                fontSize = 24.sp,
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                letterSpacing = 0.4.sp,
                lineHeight = 32.sp
            )
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .shadow(
                        elevation = 4.dp, shape = RoundedCornerShape(20.dp), clip = true
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                when {
                    isWeatherLoading -> {
                        // 로딩 중
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(SkeletonComponent()),
                            contentAlignment = Alignment.Center

                        ) {
                            Text(
                                text = "날씨 정보를 불러오는 중이에요...☁️",
                                fontSize = 11.sp,
                                fontFamily = nanum_square_neo,
                                color = Color.Gray
                            )
                        }
                    }

                    weatherState.weatherInfo != null -> {
                        // 날씨 정보 있음
                        WeatherCardView(weatherState.weatherInfo!!)
                    }

                    weatherState.errorMessage != null -> {
                        // 날씨 정보 없음 (API 실패)
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = weatherState.errorMessage ?: "날씨 정보를 불러오지 못했어요.",
                                fontSize = 11.sp,
                                fontFamily = nanum_square_neo,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // 하단 기기 작동 상태 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                when {
                    !isLinked -> {
                        NotLinkedHomeScreen(
                            onClickLink = {
                                deviceViewModel.requestAuthAndOpen(context)
                            },
                            deviceViewModel,
                            context
                        )
                    }

                    filteredDevices.isEmpty() -> {
                        // 로딩 중일 때 보여줄 UI
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(Color.White),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .background(SkeletonComponent(), RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "기기 상태를 불러오는 중이에요...☁️",
                                    fontSize = 12.sp,
                                    fontFamily = nanum_square_neo,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    else -> {
                        val myDevices = filteredDevices.map { it }
                        DeviceGridHomeSection(
                            devices = myDevices,
                            selectedDeviceId = deviceViewModel.getSelectedDevice(myDevices)?.deviceId,
                            onDeviceClick = {
                                when (it.deviceType) {
                                    DeviceListType.AIRPURIFIER -> navController.navigate("AIRPURIFIER/${it.deviceId}") {
                                        popUpTo("splash") { inclusive = true }
                                    }

                                    DeviceListType.LIGHT -> navController.navigate("LIGHT/${it.deviceId}") {
                                        popUpTo("splash") { inclusive = true }
                                    }

                                    DeviceListType.AUDIO -> navController.navigate("AUDIO/${it.deviceId}") {
                                        popUpTo("splash") { inclusive = true }
                                    }

                                    DeviceListType.SWITCH -> navController.navigate("SWITCH/${it.deviceId}") {
                                        popUpTo("splash") { inclusive = true }
                                    }

                                    DeviceListType.ETC -> {}
                                }
                            },
                            onToggleDevice = {
                                deviceViewModel.toggleDeviceState(it.deviceId, it.deviceType)
                            }
                        )
                    }
                }
            }
        }

        var showAlreadySelectedDialog by remember { mutableStateOf(false) }
        if (showAlreadySelectedDialog) {
            CommonDialog(
                showDialog = true,
                onDismiss = {
                    showAlreadySelectedDialog = false
                }, titleText = "인터넷이 연결되어 있지 않습니다!",
                bodyText = "와이파이 또는 셀룰러 연결 상태를 확인해주세요"
            )
        }


    }
}


@Composable
fun NotLinkedHomeScreen(onClickLink: () -> Unit, viewModel: DeviceListViewModel, context: Context) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 80.dp), // 네비게이션 높이 제외
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_alert_bubble),
                contentDescription = null
            )

            Spacer(modifier = Modifier.height(17.dp))

            Text(
                text = "SmartThings 계정이 아직 연동되지 않았어요.\n기기 작동 상태를 불러오기 위해 먼저 계정을 연동해주세요!",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF606060),
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(modifier = Modifier
                .background(
                    color = Color(0x1A1A1C3A), shape = RoundedCornerShape(size = 10.dp)
                )
                .clip(RoundedCornerShape(size = 10.dp))
                .clickable {
                    // smartthings 계정 연동 이동
                    viewModel.requestAuthAndOpen(context = context)
                }
                .padding(horizontal = 8.dp, vertical = 5.dp)
            )
            {
                Text(
                    text = "계정 연동하고 기기 불러오기",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 18.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF4B5BA9),
                        textAlign = TextAlign.Center,
                    ),
                )
            }

        }
    }
}

