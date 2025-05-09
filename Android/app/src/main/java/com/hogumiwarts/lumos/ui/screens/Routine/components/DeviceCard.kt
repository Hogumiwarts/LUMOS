package com.hogumiwarts.lumos.ui.screens.Routine.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceCard(
    routineDevice: RoutineDevice,
) {
    val deviceType = DeviceType.from(routineDevice.deviceType)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(106.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(10.dp),
                spotColor = Color(0x66000000), // 더 진하게 하고 싶으면 숫자 99로 바꾸기
                ambientColor = Color(0x66000000)
            )
            .background(
                color = Color.White,
                shape = RoundedCornerShape(10.dp)
            )
            .graphicsLayer {
                clip = false
            }
    ) {
        // 좌측의 기기 타입별 색 표시
        Box(
            modifier = Modifier
                .width(10.dp)
                .fillMaxHeight()
                .background(deviceType.color)
        )

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 21.dp, horizontal = 35.dp)
            ) {
                // 기기 커스텀 이름
                Text(
                    text = routineDevice.deviceName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF000000)
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                // 기기 타입
                Text(
                    text = routineDevice.deviceType,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFA1A1A1)
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                // on/off 여부
                Text(
                    text = if (routineDevice.isOn) "ON" else "OFF",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = if (routineDevice.isOn) Color(0xFFFFA754) else Color(0xFFA1A1A1)
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 기기 타입별 icon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, end = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = deviceType.iconResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 380,
    heightDp = 862
)
@Composable
fun DeviceCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        DeviceCard(
            routineDevice = RoutineDevice.sample[0]
        )
    }

}
