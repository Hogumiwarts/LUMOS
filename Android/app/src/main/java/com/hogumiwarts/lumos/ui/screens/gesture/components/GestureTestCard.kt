package com.hogumiwarts.lumos.ui.screens.gesture.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.lumos.GestureTestViewModel
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.gesture.network.MessageReceiver
import com.hogumiwarts.lumos.ui.screens.gesture.network.sendTextToWatch
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo


@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun GestureTestCard(
    card: GestureData,
    modifier: Modifier = Modifier,
    isCardFocused: Boolean,
    onclick: () -> Unit,
    viewModel: GestureTestViewModel,
) {
    val cornerRadius = 20.dp

    val message by viewModel.message


    ConstraintLayout(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
            .aspectRatio(0.605f)
            .clip(RoundedCornerShape(cornerRadius))
            .shadow(
                elevation = 50.dp,
                spotColor = Color(0x1A000000),
                ambientColor = Color(0x1A000000)
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        Color(0xFFE0E6FF),
                        Color(0xFFC4CEFF),
                        Color(0xFFAEBCFF),
                        Color(0xFF9BACFF),
                        Color(0xFF8499FF)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(Color(0x33DCDFF6))
    ) {
        if (!isCardFocused) {
            // 이미지 + 텍스트 + 테스트 버튼 구성
            val (routine, image, name, test) = createRefs()


            if (card.routineName != "") {
                Box(
                    modifier = Modifier
                        .constrainAs(routine) {
                            top.linkTo(parent.top, margin = (-25).dp)
                            bottom.linkTo(image.top)
                        }
                        .shadow(
                            elevation = 50.dp,
                            spotColor = Color(0x1A000000),
                            ambientColor = Color(0x1A000000)
                        )
                        .background(
                            color = Color(0xFF7A80AD).copy(alpha = 0.7f), // 배경 색상 (70% 투명도 예시)
                            shape = RoundedCornerShape(
                                topEnd = 8.dp,
                                bottomEnd = 8.dp,
                                topStart = 0.dp,
                                bottomStart = 0.dp
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {

                    Text(
                        text = "📎  ${card.routineName} 루틴",
                        style = TextStyle(
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight(700),
                            color = Color(0xFFFFFFFF),
                            textAlign = TextAlign.Center,
                        )
                    )
                }
            }

            AsyncImage(model = card.gestureImageUrl,
                contentDescription = "제스처 이미지",
                modifier = Modifier
                    .size(200.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom, margin = 100.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Column(
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(image.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(test.top)
                },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    card.gestureName.trim(),
                    style = TextStyle(
                        fontSize = 18.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    card.gestureDescription,
                    style = TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(400),
                        color = Color(0xB2FFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )
            }

            val context = LocalContext.current
            // 테스트 버튼 클릭 시 선택 상태로 전환
            Button(
                onClick = {
                    onclick()
                    sendTextToWatch(context, "${card.gestureId}", "${card.gestureImageUrl}")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x10FFFFFF)),
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier
                    .constrainAs(test) {
                        top.linkTo(name.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    }
                    .height(30.dp)
            ) {
                Text(
                    "테스트 해보기",
                    style = TextStyle(
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(700),
                        color = Color(0xB2FFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )
            }

        } else {

            val (image, name, content, case) = createRefs()

            Text(
                text = card.gestureName,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(name) {
                    bottom.linkTo(image.top)
                    top.linkTo(parent.top, margin = 25.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

//            Image(
//                painter = painterResource(id = R.drawable.ic_gesture), // Todo 이미지url 변경
//                contentDescription = null,
//                modifier = Modifier
//                    .size(200.dp)
//                    .constrainAs(image) {
//                        top.linkTo(parent.top)
//                        bottom.linkTo(parent.bottom, margin = 150.dp)
//                        start.linkTo(parent.start)
//                        end.linkTo(parent.end)
//                    }
//            )

            AsyncImage(model = card.gestureImageUrl,
                contentDescription = "제스처 이미지",
                modifier = Modifier
                    .size(200.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom, margin = 150.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = "지금 ${card.gestureName}을 해보세요.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(image.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )



            Box(
                modifier = Modifier.constrainAs(case) {
                    top.linkTo(content.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom, margin = 20.dp)
                }
            ) {


                Crossfade(targetState = message) { state ->
                    when (state) {
                        "done" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_recognize),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "제스처 인식 완료!",
                                    color = Color(0xFF2CDF33),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                )
                                Spacer(modifier = Modifier.size(20.dp))
                            }
                        }

                        "fail" -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                Image(
                                    painter = painterResource(id = R.drawable.ic_error),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(100.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "제스처 인식 실패!",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Thin,
                                )
                                val context = LocalContext.current
                                Text(
                                    text = "다시 시도하기",
                                    color = Color(0xFFFF5252),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.W900,
                                    modifier = Modifier.clickable {
                                        viewModel.updateMessage("")

                                        sendTextToWatch(
                                            context,
                                            "${card.gestureId}",
                                            card.gestureImageUrl
                                        )
                                    }

                                )
                                Spacer(modifier = Modifier.size(20.dp))
                            }


                        }

                        else -> {
                            val composition by rememberLottieComposition(
                                LottieCompositionSpec.RawRes(
                                    R.raw.gesture_lottie
                                )
                            )
                            val progress by animateLottieCompositionAsState(
                                composition,
                                iterations = LottieConstants.IterateForever
                            )

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                LottieAnimation(
                                    composition = composition,
                                    progress = progress,
                                    modifier = Modifier
                                        .size(80.dp)
                                )

                                Spacer(modifier = Modifier.size(24.dp))
                                Text(
                                    text = "제스처 인식중...",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }

                        }
                    }
                }


                MessageReceiver(viewModel = viewModel)
            }
        }
    }
}