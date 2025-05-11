package com.hogumiwarts.lumos.ui.screens.Home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import com.hogumiwarts.lumos.utils.CommonUtils
import com.hogumiwarts.lumos.utils.getCurrentLocation
import org.orbitmvi.orbit.compose.collectAsState
import timber.log.Timber

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val weatherState by homeViewModel.collectAsState()

    LaunchedEffect(Unit) {
        val location = getCurrentLocation(context)

        if (location != null) {
            Timber.tag("HomeScreen").d("lat: ${location.latitude}, ${location.longitude}")
            homeViewModel.onIntent(
                HomeIntent.LoadWeather(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        } else {
            Toast.makeText(context, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
//            .statusBarsPadding()
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
            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "XX님\n집에 돌아오신 걸 환영해요.",
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
                    .height(140.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = true
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 날씨 정보
                    Column {
                        Text(
                            text = weatherState.weatherInfo?.cityName ?: "ㅇㅅㅇ"
                        )
                        Text(
                            text = "${weatherState.weatherInfo?.currentTemp} °C" ?: "몇도?"
                        )
                        Text(
                            text = "${weatherState.weatherInfo?.minTemp}°C / ${weatherState.weatherInfo?.maxTemp}°C",
                        )
                    }

                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            Text(
                text = "현재 작동 상태",
                fontSize = 18.sp,
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))


            // 등록된 장치가 있는지 확인
            val devices = listOf("거실 조명", "내 방 조명", "주방 조명", "안방 조명")
            LazyVerticalGrid(
                modifier = Modifier.fillMaxWidth(),
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(
                    bottom = 12.dp
                )
            ) {
                items(devices) {
//                    LightDeviceItem()
                }
            }

            Image(
                painter = painterResource(R.drawable.img_broken_link),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "SmartThings 계정이 아직 연동되지 않았어요.\n" +
                        "기기를 불러오기 위해 먼저 계정을 연동해주세요!",
                textAlign = TextAlign.Center,
                fontFamily = nanum_square_neo,
                color = colorResource(R.color.gray_medium),
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .background(
                        color = colorResource(R.color.on_toggle_color),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "계정 연동해서 불러오기",
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

            }

        }

    }
}

@Preview(showBackground = true)
@Composable
private fun HomePreview() {
    HomeScreen()
}