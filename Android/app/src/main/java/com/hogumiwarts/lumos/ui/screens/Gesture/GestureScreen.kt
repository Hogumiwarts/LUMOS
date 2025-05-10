package com.hogumiwarts.lumos.ui.screens.Gesture

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import kotlinx.coroutines.launch
import kotlin.math.abs
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.wearable.Wearable
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.lumos.GestureTestViewModel
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginViewModel
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme
import kotlinx.coroutines.flow.collectLatest


@Composable
fun GestureScreen(viewModel: GestureViewModel = hiltViewModel()) {


    LaunchedEffect(Unit) {
        viewModel.channel.send(GestureIntent.LoadGesture)
    }

    val state by viewModel.state.collectAsState()
    when (state) {
        is GestureState.Idle -> {
            // 아무 것도 안함 (초기 상태)
        }

        is GestureState.Loading -> {
            // 🔄 로딩 UI 표시
            CircularProgressIndicator()
        }
        is GestureState.LoadedGesture ->{
            CircularCarouselWithScaling((state as GestureState.LoadedGesture).data)
        }

    }




}

@Composable
fun CircularCarouselWithScaling(cards: List<GestureResult>) {

    val viewModel: GestureTestViewModel = viewModel()
    // 무한 스크롤 가능한 Pager 상태 설정
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    // 카드가 선택된 상태를 저장하는 상태 변수
    var isCardFocused by remember { mutableStateOf(false) }

    // 화면 전체를 제약 조건으로 구성
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (title, card, select) = createRefs()

        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_gesture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 상단 제목 및 설명 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(title) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 70.dp)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCardFocused) "제스처 테스트" else "제스처 선택",
                fontSize = 25.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(
                text = if (isCardFocused)
                    "선택 전에 직접 제스처를 체험해보세요."
                else
                    "원하는 제스처를 선택해 기기를 제어하세요.",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        // 패딩과 간격 애니메이션 (선택 시 변경)
        val animatedPadding by animateDpAsState(
            targetValue = if (isCardFocused) 40.dp else 50.dp,
            animationSpec = tween(durationMillis = 300),
            label = "paddingAnimation"
        )
        val animatedSpacing by animateDpAsState(
            targetValue = if (isCardFocused) 0.dp else (-20).dp,
            animationSpec = tween(durationMillis = 300),
            label = "spacingAnimation"
        )

        // 제스처 카드 수평 Pager
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = animatedPadding),
            pageSpacing = animatedSpacing,
            userScrollEnabled = !isCardFocused, // 선택되면 스크롤 금지
            modifier = Modifier.constrainAs(card) {
                top.linkTo(title.bottom, margin = 40.dp)
            }
        ) { page ->
            val actualPage = page % cards.size
            val currentPageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = 1f - (0.2f * abs(currentPageOffset)) // 가운데 카드 확대 효과

            GestureCard(
                card = cards[actualPage],
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.coerceIn(0.8f, 1f)
                        scaleY = scale.coerceIn(0.8f, 1f)
                    }
                    .clickable(
                        enabled = isCardFocused, // 선택된 상태일 때만 클릭 가능
                        onClick = { isCardFocused = false } // 다시 선택 해제
                    ),
                isCardFocused = isCardFocused,
                viewModel = viewModel,
                onclick = {
                    isCardFocused = true

                } // "테스트 해보기" 버튼 클릭 시 실행
            )
        }

        // 하단 "선택하기" 버튼 (카드 선택 전만 표시)
        if (!isCardFocused) {
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff3E4784)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.constrainAs(select) {
                    top.linkTo(card.bottom, margin = 40.dp)
                    start.linkTo(parent.start, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text("선택하기", color = Color.White, fontSize = 18.sp)
            }
        }
    }
}

@Composable
fun GestureCard(
    card: GestureResult,
    modifier: Modifier = Modifier,
    isCardFocused: Boolean,
    onclick: () -> Unit,
    viewModel: GestureTestViewModel
) {
    val cornerRadius = 20.dp

    val message by viewModel.message
    ConstraintLayout(
        modifier = modifier
            .padding(10.dp)
            .fillMaxWidth()
            .aspectRatio(0.6f)
            .clip(RoundedCornerShape(cornerRadius))
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
            .background(Color(0x20DCDFF6))
    ) {
        if (!isCardFocused) {
            // 이미지 + 텍스트 + 테스트 버튼 구성
            val (routine,image, name, test) = createRefs()


            Box(
                modifier = Modifier.constrainAs(routine){
                    top.linkTo(parent.top, margin = 0.dp)
                    bottom.linkTo(image.top)
                }
                    .background(
                        color = Color(0xFF7A80AD).copy(alpha = 0.7f), // 배경 색상 (70% 투명도 예시)
                        shape = RoundedCornerShape(
                            topEnd = 8.dp,
                            bottomEnd = 8.dp,
                            topStart = 0.dp,
                            bottomStart = 0.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "1번 루틴과 연결",
                    color = Color.White
                )
            }

            Image(
                painter = painterResource(id = R.drawable.ic_gesture), // Todo 이미지 Url 변경
                contentDescription = null,
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
                Text(card.gestureName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(card.description, color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
            }

            val context = LocalContext.current
            // 테스트 버튼 클릭 시 선택 상태로 전환
            Button(
                onClick = {
                    onclick()
                    sendTextToWatch(context,"gd")
                          },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x10FFFFFF)),
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.constrainAs(test) {
                    top.linkTo(name.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Text("테스트 해보기", color = Color(0xB3FFFFFF), fontSize = 11.sp)
            }

        } else {
            // 선택 상태일 때: 제스처 테스트 화면
            val (image, name, content, lottie, state) = createRefs()

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

            Image(
                painter = painterResource(id = R.drawable.ic_gesture), // Todo 이미지url 변경
                contentDescription = null,
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
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(content) {
                    top.linkTo(image.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.gesture_lottie))
            val progress by animateLottieCompositionAsState(
                composition,
                iterations = LottieConstants.IterateForever
            )

            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(lottie) {
                        top.linkTo(content.bottom, margin = 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = message,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(state) {
                    top.linkTo(lottie.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )

            MessageReceiver(viewModel = viewModel)
        }
    }
}

@Composable
fun MessageReceiver(viewModel: GestureTestViewModel) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val msg = intent.getStringExtra("message") ?: return
                Log.d("TAG", "onReceive: $msg")
                viewModel.updateMessage(msg)
            }
        }

        val filter = IntentFilter("WATCH_MESSAGE")
        context.registerReceiver(receiver, filter)

        onDispose {
            context.unregisterReceiver(receiver)
        }
    }
}

fun sendTextToWatch(context: Context, message: String) {
    val messageClient = Wearable.getMessageClient(context)
    val path = "/launch_text_display"


    // 워치 노드 가져오기
    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            for (node in nodes) {
                messageClient.sendMessage(node.id, path, message.toByteArray())
                    .addOnSuccessListener {
                        Log.d("Mobile", "메시지 전송 성공")
                    }
                    .addOnFailureListener {
                        Log.e("Mobile", "메시지 전송 실패: ${it.message}")
                    }
            }
        }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent
        ) {
            GestureScreen()
        }
    }
}
