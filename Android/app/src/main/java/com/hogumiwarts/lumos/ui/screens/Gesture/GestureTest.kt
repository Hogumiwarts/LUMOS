package com.hogumiwarts.lumos.ui.screens.Gesture

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.lumos.GestureTestViewModel
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.Gesture.components.GestureTestCard
import kotlin.math.abs

@Composable
fun GestureTest(cards: List<GestureData>) {

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

            GestureTestCard(
                card = cards[actualPage],
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.coerceIn(0.8f, 1f)
                        scaleY = scale.coerceIn(0.8f, 1f)
                    }
                    .clickable(
                        enabled = isCardFocused, // 선택된 상태일 때만 클릭 가능
                        onClick = {
                            isCardFocused = false
                            viewModel.updateMessage("")
                        } // 다시 선택 해제
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

