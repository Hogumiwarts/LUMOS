package com.hogumiwarts.lumos.ui.screens.routine.routineDeviceList.devicecontrolscreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.mapper.toCommandDeviceForSpeaker
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.screens.control.audio.AudioIntent
import com.hogumiwarts.lumos.ui.screens.control.audio.SpeakerDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import com.hogumiwarts.lumos.ui.viewmodel.AudioViewModel

@Composable
fun PreviewSpeakerScreenContent(
    navController: NavController,
    selectedDevice: MyDevice,
    viewModel: AudioViewModel = hiltViewModel()
) {
    // todo: 실제 최근 재생 곡 정보 받아오기
    val speakerDevice = remember {
        SpeakerDevice(
            tagNumber = 1,
            deviceId = 12345,
            deviceImg = "https://storage.googleapis.com/lumos-assets/devices/smart_speaker.png",
            deviceName = "무드 플레이어",
            manufacturerCode = "Sonos",
            deviceModel = "SYMFONISK_V2",
            deviceType = "AUDIO",
            activated = true,
            audioImg = "https://storage.googleapis.com/lumos-assets/albums/weekend_vibes.jpg",
            audioName = "WISH",
            audioArtist = "NCT WISH",
            audioVolume = 30
        )
    }

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AudioIntent.LoadAudioStatus(selectedDevice.deviceId))
    }

    var volume by remember { mutableIntStateOf(speakerDevice.audioVolume) }
    var isMuted by remember { mutableStateOf(false) }
    var isPlaying: Boolean? by remember { mutableStateOf(null) } // null: 선택 전
    val primaryColor = Color(0xFF4A5BB9)

    var audioImage by remember { mutableStateOf(speakerDevice.audioImg) }
    var audioName by remember { mutableStateOf(speakerDevice.audioName) }
    var audioArtist by remember { mutableStateOf(speakerDevice.audioArtist) }

    // 음소거 상태에 따라 UI 노출 여부 조절
    val showControls = !isMuted

    // card 테두리 색상
    val gradientColors = listOf(Color(0xFFDCDFF6), Color(0xFF717BBC))
    val gradientBrush = Brush.horizontalGradient(colors = gradientColors)

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("알림") },
            text = { Text("재생 또는 일시정지 상태를 선택해주세요.") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("확인")
                }
            }
        )
    }


    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = speakerDevice.deviceName,
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "스피커",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF000000),
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f)
                        .padding(vertical = 8.dp)
                        .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                        .border(
                            width = 2.dp,
                            brush = gradientBrush,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        //커버
                        AsyncImage(
                            model = audioImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )

                        // 어두운 반투명 오버레이
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.3f))
                        )

                        // 타이틀 & 가수
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 25.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = audioName,
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = nanum_square_neo,
                                        fontWeight = FontWeight(800),
                                        color = Color.White,
                                    )
                                )

                                Spacer(modifier = Modifier.height(7.dp))

                                Text(
                                    text = audioArtist,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        lineHeight = 16.sp,
                                        fontFamily = nanum_square_neo,
                                        fontWeight = FontWeight(700),
                                        color = Color(0xFFE1E1E1),
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            //재생 or 일시정지
                            Column(
                                modifier = Modifier
                                    .width(70.dp)
                                    .height(70.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                listOf(true to "재생", false to "일시정지").forEach { (state, label) ->
                                    val selected = isPlaying == state
                                    val borderColor =
                                        if (selected) primaryColor else Color(0xFFD9DCE8)
                                    val backgroundColor =
                                        if (selected) primaryColor else Color.White
                                    val textColor = if (selected) Color.White else Color.Black

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxWidth()
                                            .border(
                                                width = 1.dp,
                                                color = borderColor,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .background(
                                                color = backgroundColor,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = rememberRipple(
                                                    bounded = true,
                                                    color = primaryColor
                                                )
                                            ) {
                                                isPlaying = state
                                            }
                                            .padding(vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            color = textColor,
                                            fontFamily = nanum_square_neo,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }

                        }
                    }

                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "※ 최근 재생된 곡으로 실제 재생 곡과 다를 수 있어요.",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color(0xFF9A9A9A),
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(500),
                    ),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

// Volume Section

            AnimatedVisibility(
                visible = isPlaying != false && !isMuted,
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "볼륨",
                        style = TextStyle(
                            fontSize = 18.sp,
                            lineHeight = 16.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(800),
                            color = Color(0xFF000000),
                        )
                    )

                    Spacer(modifier = Modifier.padding(horizontal = 5.dp))

                    Row(
                        modifier = Modifier
                            .wrapContentWidth() // Row 너비를 내용물에 맞춤
                            .border(
                                1.dp,
                                color = if (isMuted) Color(0xFF4B5BA9) else Color(0xffD5D9EB),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                // 음소거 토글 및 볼륨 처리
                                isMuted = !isMuted
                                if (isMuted) {
                                    // 음소거 시 볼륨 0으로 설정
                                    volume = 0
                                }
                            }
                            .background(
                                color = if (isMuted) Color(0xff4B5BA9) else Color(0xffFFFDFD),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .align(Alignment.CenterVertically)
                            .clip(RoundedCornerShape(20.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_volumn_off),
                                contentDescription = if (isMuted) "음소거 해제" else "음소거",
                                tint = if (isMuted) Color.White else Color(0xff4B5BA9),
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = null,
                                        role = Role.Button,
                                        onClick = {
                                            isMuted = true
                                        }
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        androidx.compose.material.Text(
                            "음소거", fontSize = 12.sp,
                            color = if (isMuted) Color.White else Color(0xff4B5BA9),
                        )
                    }

                }

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_volumn_zero),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Slider(
                            value = volume.toFloat(),
                            modifier = Modifier.weight(1f),
                            onValueChange = {
                                volume = it.toInt()
                                isMuted = it == 0f
                            },
                            valueRange = 0f..100f,
                            steps = 0,
                            colors = SliderDefaults.colors(
                                thumbColor = primaryColor,
                                activeTrackColor = primaryColor,
                                inactiveTrackColor = Color.LightGray
                            ),
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "0%",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = nanum_square_neo,
                                fontWeight = FontWeight(350),
                                color = Color(0xFFB9C0D4),
                                textAlign = TextAlign.Center,
                            )
                        )
                        Text(
                            text = "100%",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontFamily = nanum_square_neo,
                                fontWeight = FontWeight(350),
                                color = Color(0xFFB9C0D4),
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(15.dp))
            Spacer(modifier = Modifier.height(24.dp))

            // 기기 정보
            Text(
                text = "기기 정보",
                style = TextStyle(
                    fontSize = 18.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF000000),
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "제조사 | ${speakerDevice.manufacturerCode}",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),

                    )
            )

            Spacer(modifier = Modifier.height(5.dp))

            androidx.compose.material.Text(
                text = "모델명 | ${speakerDevice.deviceModel}",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),

                    )
            )

            Spacer(modifier = Modifier.height(5.dp))

            androidx.compose.material.Text(
                text = "연결방식 | Wi-Fi",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),

                    )
            )

            Spacer(modifier = Modifier.height(5.dp))

            androidx.compose.material.Text(
                text = "버전 | 1.0.0",
                style = TextStyle(
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF000000),

                    )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 하단 고정 버튼
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            PrimaryButton(
                buttonText = "설정하기",
                onClick = {
                    if (isPlaying == null) {
                        showDialog = true
                        return@PrimaryButton
                    }

                    val commandDevice = selectedDevice.toCommandDeviceForSpeaker(
                        isOn = !isMuted,
                        volume = volume,
                        isPlaying = isPlaying!!
                    )

                    val json = Gson().toJson(commandDevice)
                    println("📤 JSON 데이터: $json")

                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "commandDeviceJson",
                        json
                    )

                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 27.dp)
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun PreviewSpeakerScreenPreview() {
    val dummyDevice = MyDevice(
        deviceId = 6,
        deviceName = "무드 플레이어",
        deviceType = DeviceListType.AUDIO,
        isOn = true,
        isActive = true,
        commands = emptyList()
    )

    PreviewSpeakerScreenContent(
        navController = rememberNavController(),
        selectedDevice = dummyDevice
    )
}
