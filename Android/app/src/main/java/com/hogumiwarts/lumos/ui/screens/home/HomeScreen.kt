package com.hogumiwarts.lumos.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.hogumiwarts.domain.model.WeatherInfo
import com.hogumiwarts.lumos.DataStore.TokenDataStore
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.DeviceGridHomeSection
import com.hogumiwarts.lumos.ui.common.DeviceGridSection
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.SkeletonComponent
import com.hogumiwarts.lumos.ui.screens.control.ControlViewModel
import com.hogumiwarts.lumos.ui.screens.devices.DeviceListViewModel
import com.hogumiwarts.lumos.ui.screens.devices.NotLinkedScreen
import com.hogumiwarts.lumos.ui.screens.home.components.LightDeviceItem
import com.hogumiwarts.lumos.ui.screens.home.components.WeatherCardView
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceType
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
   tokenDataStore: TokenDataStore
    ) {
    val context = LocalContext.current
    val weatherState by homeViewModel.collectAsState()
    val isWeatherLoading = weatherState.isLoading

    val isLinked by deviceViewModel.isLinked.collectAsState()
    val deviceList by deviceViewModel.deviceList.collectAsState()

    val HomeState by homeViewModel.collectAsState()


    LaunchedEffect(Unit) {
        controlViewModel.prepareSession()
        deviceViewModel.checkAccountLinked()

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
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = CommonUtils.getFormattedToday(),
                fontSize = 14.sp,
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
            Spacer(modifier = Modifier.height(36.dp))


            Text(
                text = "${HomeState.userName ?: "루모스"}님 ${controlViewModel.localAddress}\n집에 돌아오신 걸 환영해요. ",
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
                    .height(148.dp)
                    .shadow(
                        elevation = 4.dp, shape = RoundedCornerShape(20.dp), clip = true
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                if (isWeatherLoading && weatherState.weatherInfo == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(SkeletonComponent())
                    )
                } else {
                    weatherState.weatherInfo?.let { WeatherCardView(it) }
                }
            }

            // 하단 기기 작동 상태 영역
            Box(
            ) {
                if (!isLinked) {
                    NotLinkedHomeScreen(
                        onClickLink = {
                            deviceViewModel.requestAuthAndOpen(context)
                        }, deviceViewModel, context
                    )
                } else {
                    val myDevices = deviceList.map { it }
                    DeviceGridHomeSection(
                        devices = myDevices,
                        selectedDeviceId = deviceViewModel.getSelectedDevice(myDevices)?.deviceId,
                        onDeviceClick = { deviceViewModel.onDeviceClicked(it) },
                        onToggleDevice = { device ->
                            // viewModel에서 상태 반전 요청
                            deviceViewModel.toggleDeviceState(device.deviceId)
                        }
                    )
                }
            }
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
                .padding(horizontal = 8.dp, vertical = 5.dp)
                .clickable {
                    // smartthings 계정 연동 이동
                    viewModel.requestAuthAndOpen(context = context)
                }) {
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

