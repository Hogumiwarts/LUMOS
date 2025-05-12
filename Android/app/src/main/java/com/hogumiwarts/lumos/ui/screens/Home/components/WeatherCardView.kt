package com.hogumiwarts.lumos.ui.screens.Home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.domain.model.WeatherInfo
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.Home.HomeScreen
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import com.hogumiwarts.lumos.utils.CommonUtils

@Composable
fun WeatherCardView(weatherInfo: WeatherInfo) {
    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_sun_wind),
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
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
                .padding(end = 12.dp, top = 12.dp, bottom = 12.dp)
        ) {
            Text(
                text = "구미시",
                fontFamily = nanum_square_neo,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = colorResource(id = R.color.gray_light),
                letterSpacing = 0.4.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${weatherInfo.currentTemp}°C",
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier.alignBy(LastBaseline),
                    letterSpacing = 0.4.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${weatherInfo.minTemp - 8}°C / ${weatherInfo.maxTemp + 3}°C",
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Normal,
                    fontSize = 10.sp,
                    color = colorResource(id = R.color.gray_medium),
                    modifier = Modifier.alignBy(LastBaseline),
                    letterSpacing = 0.4.sp
                )
            }
            Row(modifier = Modifier.padding(top = 10.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
//                    Text(
//                        text = "강수확률",
//                        fontFamily = nanum_square_neo,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 13.sp,
//                        color = colorResource(id = R.color.gray_medium),
//                        letterSpacing = 0.4.sp
//                    )
                    Text(
                        text = "강수확률",
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorResource(id = R.color.gray_medium),
                        letterSpacing = 0.4.sp
                    )
                    Text(
                        text = "습도",
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorResource(id = R.color.gray_medium),
                        letterSpacing = 0.4.sp
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
//                    Text(
//                        text = "좋음",
//                        fontFamily = nanum_square_neo,
//                        fontWeight = FontWeight.Bold,
//                        fontSize = 13.sp,
//                        color = colorResource(id = R.color.gray_medium),
//                        letterSpacing = 0.4.sp
//                    )
                    Text(
                        text = "${weatherInfo.rainProbability}%",
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorResource(id = R.color.gray_medium),
                        letterSpacing = 0.4.sp
                    )
                    Text(
                        text = "${weatherInfo.humidity}%",
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colorResource(id = R.color.gray_medium),
                        letterSpacing = 0.4.sp
                    )
                }
            }

        }
    }
}