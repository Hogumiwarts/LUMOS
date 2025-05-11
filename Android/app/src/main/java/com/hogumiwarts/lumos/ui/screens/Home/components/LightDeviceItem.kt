package com.hogumiwarts.lumos.ui.screens.Home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
fun LightDeviceItem() {
    var isOnToggle by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(20.dp),
                clip = true
            )
            .border(
                width = 1.dp,
                color = colorResource(R.color.point_primary),
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
                    checkedTrackColor = colorResource(R.color.point_primary),
                    uncheckedTrackColor = Color.LightGray,
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
                    "거실 조명",
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(0.6f),
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "조명",
                    fontFamily = nanum_square_neo,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Box(modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 12.dp)

            ) {
                Image(
                    painter = painterResource(R.drawable.ic_light_off),
                    contentDescription = null,
                    )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CardPreview() {
    LightDeviceItem()
}