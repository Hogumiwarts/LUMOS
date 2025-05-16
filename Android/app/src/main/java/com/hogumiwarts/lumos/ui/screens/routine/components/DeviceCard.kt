package com.hogumiwarts.lumos.ui.screens.routine.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.domain.model.CommandData
import com.hogumiwarts.domain.model.CommandDevice
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import timber.log.Timber

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceCard(
    commandDevice: CommandDevice,
    deviceType: DeviceListType,
    ) {

    val iconResId = deviceType.iconResId
    val color = deviceType.color
    val deviceTypeName = deviceType.deviceName

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
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
                .background(color)
        )

        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 15.dp, horizontal = 35.dp)
            ) {
                // 기기 커스텀 이름
                Text(
                    text = commandDevice.deviceName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700),
                        color = Color(0xFF000000)
                    )
                )

                Spacer(modifier = Modifier.height(5.dp))

                // 기기 타입
                Text(
                    text = deviceTypeName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = nanum_square_neo,
                        fontSize = 11.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFFA1A1A1)
                    )
                )


                Spacer(modifier = Modifier.weight(1f))

                // on/off 여부
                val commandText = commandDevice.commands.joinToString(", ") {
                    getKoreanDescription(it)
                }

                Text(
                    text = commandText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFFFFA754)
                    )
                )


            }

            Spacer(modifier = Modifier.weight(1f))

            // 기기 타입별 icon
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 10.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Image(
                    painter = painterResource(id = iconResId),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        }
    }
}


// command 내용에서 사용자가 지정한 행동 한국어로 추출
fun getKoreanDescription(command: CommandData): String {
    return when (command.capability to command.command) {
        "switch" to "on" -> "전원 켜기"
        "switch" to "off" -> "전원 끄기"
        "colorControl" to "setColor" -> "조명 색상 설정"
        "switchLevel" to "setLevel" -> "밝기 조절"
        "mediaPlayback" to "play" -> "재생"
        "mediaPlayback" to "stop" -> "정지"
        "AUDIO" to "setVolumn" -> "볼륨 조절"
        "airConditionerFanMode" to "setFanMode" -> "팬 속도: ${command.arguments.firstOrNull() ?: "알 수 없음"}"
        else -> "${command.capability}.${command.command}"
    }
}
