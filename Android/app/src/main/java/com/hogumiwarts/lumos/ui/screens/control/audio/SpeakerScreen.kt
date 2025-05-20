package com.hogumiwarts.lumos.ui.screens.control.audio

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.domain.model.audio.AudioStatusData
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.control.components.GradientCircularProgressIndicator
import com.hogumiwarts.lumos.ui.screens.control.minibig.SwitchIntent
import com.hogumiwarts.lumos.ui.viewmodel.AudioViewModel
import com.hogumiwarts.lumos.ui.viewmodel.SwitchViewModel


data class SpeakerDevice(
    val tagNumber: Int,
    val deviceId: Int,
    val deviceImg: String,
    val deviceName: String,
    val manufacturerCode: String,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean,
    val audioImg: String,
    val audioName: String,
    val audioArtist: String,
    val audioVolume: Int
)

@Composable
fun SpeakerScreen(deviceId: Long, viewModel: AudioViewModel = hiltViewModel()) {

    LaunchedEffect(Unit) {
        viewModel.sendIntent(AudioIntent.LoadAudioStatus(deviceId))
    }
    val state by viewModel.state.collectAsState()
    val playState by viewModel.playState.collectAsState()
    val volumeState by viewModel.volumeState.collectAsState()


    var speakerDevice = remember {
        AudioStatusData(
            tagNumber = 1,
            deviceId = 12345,
            deviceImg = "https://storage.googleapis.com/lumos-assets/devices/smart_speaker.png",
            deviceName = "무드 플레이어",
            manufacturerCode = "adfadf",
            deviceModel = "SYMFONISK_V2",
            deviceType = "스피커",
            activated = true,
            audioImg = "https://storage.googleapis.com/lumos-assets/albums/weekend_vibes.jpg",
            audioName = "WISH",
            audioArtist = "NCT WISH",
            audioVolume = 30
        )
    }

    var isPlaying by remember { mutableStateOf(speakerDevice.activated) }
    var volume by remember { mutableIntStateOf(speakerDevice.audioVolume) }
    var audioImage by remember { mutableStateOf(speakerDevice.audioImg) }
    var deviceName by remember { mutableStateOf(speakerDevice.deviceName) }
    var manufacturerCode by remember { mutableStateOf(speakerDevice.manufacturerCode) }
    var deviceModel by remember { mutableStateOf(speakerDevice.deviceModel) }
    var audioName by remember { mutableStateOf(speakerDevice.audioName) }
    var audioArtist by remember { mutableStateOf(speakerDevice.audioArtist) }

    LaunchedEffect(state) {
        when (state) {
            is AudioStatusState.Error -> {
                // TODO: 에러 처리
            }

            AudioStatusState.Idle -> {}
            is AudioStatusState.Loaded -> {
                val data = (state as AudioStatusState.Loaded).data
                Log.d("TAG", "loadAudioStatus: ${data}")
                speakerDevice = data
                deviceName = data.deviceName
                manufacturerCode = data.manufacturerCode
                audioArtist = data.audioArtist
                deviceModel = data.deviceModel
                audioName = data.audioName
                isPlaying = data.activated
                volume = data.audioVolume
                audioImage = data.audioImg
            }

            AudioStatusState.Loading -> {
                // TODO: 로딩 화면
            }
        }
    }

    LaunchedEffect(volumeState) {
        when (volumeState) {
            is AudioVolumeState.Error -> {
                // TODO: 에러처리 
            }

            AudioVolumeState.Idle -> {}
            is AudioVolumeState.Loaded -> {
//                volume = (volumeState as AudioVolumeState.Loaded).data.volume
            }

            AudioVolumeState.Loading -> {
                // TODO: 로딩구현 
            }
        }
    }
    LaunchedEffect(playState) {
        when (playState) {
            is AudioPlayState.Error -> {
                // TODO: 에러 처리
            }

            AudioPlayState.Idle -> {}
            is AudioPlayState.Loaded -> {
                isPlaying = !isPlaying
            }

            AudioPlayState.Loading -> {
                // TODO: 로딩 화면
            }
        }
    }

    var isMuted by remember { mutableStateOf(false) }
    val primaryColor = Color(0xFF4A5BB9)


    val infiniteTransition = rememberInfiniteTransition(label = "회전 애니메이션 트랜잭션")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 5000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    // card 테두리 색상
    val gradientColors = listOf(Color(0xFFDCDFF6), Color(0xFF717BBC))
    val gradientBrush = Brush.horizontalGradient(colors = gradientColors)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = deviceName,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "스피커",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Volume Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "볼륨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.padding(horizontal = 8.dp))

            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Button,
                        onClick = { }
                    )
                    .border(
                        1.dp,
                        color = if (isMuted) Color(0xff4B5BA9) else Color(0xffFFFDFD),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // 음소거 토글 및 볼륨 처리
                        isMuted = !isMuted
                        if(isMuted){
                            viewModel.sendIntent(AudioIntent.LoadAudioVolume(deviceId,0))
                        }else{
                            viewModel.sendIntent(AudioIntent.LoadAudioVolume(deviceId,volume))
                        }

                    }
                    .background(
                        color = if (isMuted) Color(0xffFFFDFD) else Color(0xff4B5BA9),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(start = 4.dp, end = 8.dp)
                    .clip(RoundedCornerShape(20.dp)),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier

                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_volumn_off),
                        contentDescription = if (!isMuted) "음소거 해제" else "음소거",
                        tint = if (!isMuted) Color.White else Color(0xff4B5BA9),
                        modifier = Modifier
                            .size(18.dp)

                    )
                }

                Text(

                    "음소거", fontSize = 12.sp,
                    color = if (!isMuted) Color.White else Color(0xff4B5BA9),
                    modifier = Modifier.padding(end = 8.dp),
                )
            }

        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                painter = painterResource(id = R.drawable.ic_volumn_zero),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Slider(
                value = volume.toFloat(),
                onValueChange = {
                    volume = it.toInt()
                    isMuted = false

                },
                onValueChangeFinished = {
                    if (!isMuted) {
                        Log.d("VolumeSlider", "최종 볼륨 값: $volume")
                        viewModel.sendIntent(
                            AudioIntent.LoadAudioVolume(
                                deviceId = deviceId,
                                volume = volume
                            )
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .then(if (isMuted) Modifier.alpha(0.4f) else Modifier), // 시각적 비활성화 효과


                valueRange = 0f..100f,
                steps = 0,
                colors = SliderDefaults.colors(
                    thumbColor = if (isMuted) Color.Gray else primaryColor,
                    activeTrackColor = if (isMuted) Color.Gray else primaryColor,
                    inactiveTrackColor = Color.LightGray
                ),
            )

//            Icon(
//                painter = painterResource(id = R.drawable.ic_volumn_max),
//                contentDescription = null,
//                modifier = Modifier.size(24.dp)
//            )


        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "0%",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = "100%",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
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
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(350.dp)
                        .align(Alignment.CenterStart)
                        .offset(x = (-150).dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Album image
                    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.am_lp))
                    val animatable = remember { Animatable(0f) } // 처음은 0%
                    var isFirstPlay by remember { mutableStateOf(true) }
                    val progress = animatable.value

                    LaunchedEffect(composition, isPlaying) {

                            animatable.stop() // 이전 애니메이션 중지
                            val start = if (isFirstPlay) 0f else 0.1f
                            isFirstPlay = false

                            animatable.snapTo(start)
                            animatable.animateTo(
                                targetValue = 0.9f,
                                animationSpec = tween(durationMillis = 3000)
                            )

                    }

                    if (isPlaying) {

                        Box(
                            modifier = Modifier
                                .size(250.dp)
                                .offset(x = -35.dp)
                                .rotate(rotation)
                        ) {
                            GradientCircularProgressIndicator(
                                modifier = Modifier.fillMaxSize(),
                                strokeWidth = 15f,
                                strokeCap = StrokeCap.Round
                            )
                        }

                    } else {
                        // Paused state indicator
                        CircularProgressIndicator(
                            progress = 1f,
                            modifier = Modifier.size(250.dp)
                                .offset(x = -35.dp),
                            color = Color(0xFFE0E0E0), // Gray
                            strokeWidth = 6.dp,
                            strokeCap = StrokeCap.Round,
                        )
                    }
                    LottieAnimation(
                        composition = composition,
                        progress = {progress},
                        modifier = Modifier.size(350.dp)
                    )

//                    AsyncImage(
//                        model = audioImage,
//                        contentDescription = null,
//                        modifier = Modifier.size(220.dp),
//                        contentScale = ContentScale.Fit
//                    )
//                    Image(
//                        painter = painterResource(id = R.drawable.wish),
//                        contentDescription = null,
//                        modifier = Modifier
//                            .size(220.dp)
//                            .clip(CircleShape),
//                        contentScale = ContentScale.Crop
//                    )

                    // TODO: DB에 url 받아 올때
//                    AsyncImage(model = speakerDevice.audioImg,
//                        contentDescription = "앨범 사진" )

                    // indicator

                }


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(start = 120.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(
                        modifier = Modifier.width(150.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = audioName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp,
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = audioArtist,
                            fontSize = 20.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 재생/일시정지 버튼
                    Box(
                        modifier = Modifier
                            .size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(id = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play),
                            contentDescription = if (isPlaying) "일시정지" else "재생",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,  // ripple 효과 제거
                                    role = Role.Button,
                                    onClick = {
                                        viewModel.sendIntent(
                                            AudioIntent.LoadAudioPlay(
                                                deviceId,
                                                !isPlaying
                                            )
                                        )
//                                        isPlaying = !isPlaying
                                    }
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 기기 정보
        Text(
            text = "기기 정보",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "제조사 | ${manufacturerCode}",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Text(
            text = "모델명 | ${deviceModel}",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Text(
            text = "연결방식 | Wi-Fi",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Text(
            text = "버전 | 1.0.0",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview
@Composable
fun SpeakerScreenPreview() {
    SpeakerScreen(1)
}