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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.domain.model.routine.CommandData
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceCard(
    commandDevice: CommandDevice,
    deviceType: DeviceListType,
    modifier: Modifier = Modifier
) {

    val iconResId = deviceType.iconResId
    val color = deviceType.color
    val deviceTypeName = deviceType.categoryName

    Box(
        modifier = modifier
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
                // 전원 off 상태일 경우엔 그 명령만 표시, 아니면 모두 출력
                val commandText =
                    if (commandDevice.commands.any { it.capability == "switch" && it.command == "off" }) {
                        getKoreanDescription(commandDevice.commands.first { it.capability == "switch" && it.command == "off" })
                    } else {
                        commandDevice.commands.joinToString(", ") {
                            getKoreanDescription(it)
                        }
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
        "colorControl" to "setColor" -> {
            val color = command.arguments?.firstOrNull()
            if (color is Map<*, *>) {
                val hue = color["hue"]?.toString()?.toDoubleOrNull()?.roundToInt()
                val saturation = color["saturation"]?.toString()?.toDoubleOrNull()?.roundToInt()

                if (hue != null && saturation != null) {
                    val colorName = getColorNameFromHue(hue)
                    "$colorName"
                } else {
                    "조명 색상 설정"
                }
            } else {
                "조명 색상 설정"
            }
        }

        "switchLevel" to "setLevel" -> {
            val level = command.arguments?.firstOrNull()
                ?.toString()?.toDoubleOrNull()?.roundToInt() ?: return "밝기 조절"
            "밝기 ${level}%"
        }

        "mediaPlayback" to "play" -> "재생"
        "mediaPlayback" to "stop" -> "정지"
        "audioVolume" to "setVolume" -> {
            val volume = command.arguments?.firstOrNull()
                ?.toString()?.toDoubleOrNull()?.roundToInt() ?: "알 수 없음"
            "볼륨 ${volume}으로 조절"
        }

        "airConditionerFanMode" to "setFanMode" -> "팬 속도: ${command.arguments?.firstOrNull() ?: "알 수 없음"}"
        else -> "${command.capability}.${command.command}"
    }
}

fun getColorNameFromHue(hue: Int): String {
    return when (hue) {
        in 0..15, in 331..360 -> "빨간색"
        in 16..45 -> "주황색"
        in 46..65 -> "노란색"
        in 66..170 -> "초록색"
        in 171..250 -> "파란색"
        in 251..290 -> "남색"
        in 291..330 -> "보라색"
        else -> "색상 미지정"
    }
}
