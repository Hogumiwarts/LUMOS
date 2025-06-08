package com.hogumiwarts.lumos.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.domain.model.WeatherInfo
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun WeatherCardView(weatherInfo: WeatherInfo) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(end = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sun_wind),
            contentDescription = null,
            modifier = Modifier
                .height(120.dp)
                .aspectRatio(1.2f)
                .graphicsLayer {
                    scaleX = 1.2f
                    scaleY = 1.2f
                }
                .offset(x = (-8).dp, y = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Column(
            Modifier
                .fillMaxHeight()
                .padding(end = 35.dp, top = 25.dp, bottom = 12.dp)
        ) {
            Text(
                text = "구미시",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(700),
                    color = Color(0xFFB1B1B1),
                    letterSpacing = 0.4.sp,
                )
            )

            Spacer(modifier = Modifier.height(7.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "${weatherInfo.currentTemp}°C",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF333333),
                        letterSpacing = 0.4.sp,
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "${weatherInfo.minTemp - 8}°C / ${weatherInfo.maxTemp + 3}°C",
                    style = TextStyle(
                        fontSize = 9.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF606060),
                        letterSpacing = 0.4.sp,
                    )
                )
            }

            Row(modifier = Modifier.padding(top = 10.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "강수확률",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF606060),
                            letterSpacing = 0.4.sp,
                        )
                    )
                    Text(
                        text = "습도",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF606060),
                            letterSpacing = 0.4.sp,
                        )
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "${weatherInfo.rainProbability}%",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF606060),
                            letterSpacing = 0.4.sp,
                        )
                    )
                    Text(
                        text = "${weatherInfo.humidity}%",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF606060),
                            letterSpacing = 0.4.sp,
                        )
                    )
                }
            }

        }
    }
}