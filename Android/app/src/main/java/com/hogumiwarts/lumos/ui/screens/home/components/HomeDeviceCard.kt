package com.hogumiwarts.lumos.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun HomeDeviceCard(
    deviceName: String,
    deviceType: String,
    isActive: Boolean,
    deviceImgId: Int
) {
    var isOnToggle by rememberSaveable { mutableStateOf(isActive) }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(
                elevation = if (isOnToggle) 6.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                clip = true,
                ambientColor = if (isOnToggle) colorResource(R.color.main_primary) else Color.Black,
                spotColor = if (isOnToggle) colorResource(R.color.main_primary) else Color.Black,
            )
            .border(
                width = if (isOnToggle) 1.dp else 1.dp,
                color = if (isOnToggle) colorResource(R.color.point_color) else Color(0xFFBBBFDD),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(Modifier.fillMaxSize()) {

            Switch(
                checked = isOnToggle,
                onCheckedChange = {
                    isOnToggle = it
                },
                modifier = Modifier
                    .padding(top = 20.dp)
                    .rotate(-90f),
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    uncheckedThumbColor = Color.White,
                    checkedTrackColor = colorResource(R.color.on_toggle_color),
                    uncheckedTrackColor = colorResource(R.color.off_toggle_color),
                    checkedBorderColor = Color.Transparent,
                    uncheckedBorderColor = Color.Transparent
                ),
                thumbContent = {
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = CircleShape)
                    )
                }
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 12.dp, bottom = 12.dp)

            ) {
                Text(
                    text = deviceName,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = deviceType,
                    fontFamily = nanum_square_neo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 12.dp)

            ) {
                Image(
                    painter = painterResource(deviceImgId),
                    contentDescription = null,
                    modifier = Modifier.fillMaxHeight(0.75f)
                        .offset(y = -12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardPreview() {

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp)
            .background(Color.White)
    ) {
        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(
                bottom = 12.dp
            )
        ) {
            item {
                HomeDeviceCard(
                    deviceName = "좋은 스피커",
                    deviceType = "스피커",
                    isActive = true,
                    deviceImgId = R.drawable.img_device_speaker
                )
            }

            item {
                HomeDeviceCard(
                    deviceName = "샘숭 공청",
                    deviceType = "공기청정기",
                    isActive = false,
                    deviceImgId = R.drawable.img_device_air
                )
            }

            item {
                HomeDeviceCard(
                    deviceName = "방 스위치",
                    deviceType = "스위치",
                    isActive = false,
                    deviceImgId = R.drawable.img_device_switch
                )
            }

        }
    }

}