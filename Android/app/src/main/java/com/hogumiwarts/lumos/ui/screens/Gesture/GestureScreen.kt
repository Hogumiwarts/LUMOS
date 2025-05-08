package com.hogumiwarts.lumos.ui.screens.Gesture

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.hogumiwarts.lumos.ui.theme.LUMOSTheme

// 임시 카드 데이터 모델
data class CardData(val title: String, val description: String, val imageRes: Int)

@Composable
fun GestureScreen() {
    val cards = listOf(
        CardData("핑거 스냅", "손가락을 튕겨서 명령을 실행합니다.", R.drawable.ic_sun),
        CardData("주먹 쥠", "손을 쥐어서 제어합니다.", R.drawable.ic_sun),
        CardData("손 펴기", "손을 펴서 동작을 시작합니다.", R.drawable.ic_sun)
    )

    CircularCarouselWithScaling(cards)
}

@Composable
fun CircularCarouselWithScaling(cards: List<CardData>) {
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_gesture),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val (title, card, select) = createRefs()
        var isCardFocused by remember { mutableStateOf(false) }

        var title1 = "제스처 선택"
        var description = "원하는 제스처를 선택해 기기를 제어하세요."
        if(!isCardFocused){
            title1 = "제스처 선택"
            description = "원하는 제스처를 선택해 기기를 제어하세요."
        }else{
            title1 = "제스처 테스트"
            description = "선택 전에 직접 제스처를 체험해보세요."
        }

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
                text = title1,
                fontSize = 25.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.size(16.dp))
            Text(text = description, fontSize = 16.sp, color = Color.White)
        }



        // contentPadding을 애니메이션으로 변경
        val animatedPadding by animateDpAsState(
            targetValue = if (isCardFocused) 30.dp else 50.dp,
            animationSpec = tween(durationMillis = 300),
            label = "paddingAnimation"
        )

        // contentPadding을 애니메이션으로 변경
        val animatedSpacing by animateDpAsState(
            targetValue = if (isCardFocused) 0.dp else (-20).dp,
            animationSpec = tween(durationMillis = 300),
            label = "paddingAnimation"
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = animatedPadding),
            pageSpacing = animatedSpacing,
            userScrollEnabled = !isCardFocused,
            modifier = Modifier.constrainAs(card){
                top.linkTo(title.bottom, margin = 40.dp)
            }
        ) { page ->
            val actualPage = page % cards.size
            val currentPageOffset =
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = 1f - (0.2f * kotlin.math.abs(currentPageOffset))

            GestureCard(
                card = cards[actualPage],
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale.coerceIn(0.8f, 1f)
                        scaleY = scale.coerceIn(0.8f, 1f)
                    }
                    .clickable {
                        isCardFocused = !isCardFocused // 클릭 시 확대/축소 상태 토글
                    },
                isCardFocused = isCardFocused
            )
        }

        if(!isCardFocused){
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff3E4784)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.constrainAs(select) {
                    top.linkTo(card.bottom, margin = 40.dp)
                    start.linkTo(parent.start, margin = 30.dp)
                    end.linkTo(parent.end, margin = 30.dp)
                    width = Dimension.fillToConstraints
                }
            ) {
                Text("선택하기",color= Color.White, fontSize = 18.sp)
            }

        }

    }
}

@Composable
fun GestureCard(card: CardData, modifier: Modifier = Modifier, isCardFocused: Boolean) {

    val cornerRadius = 20.dp


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
                    ) // 보라 → 남색 그라데이션
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(Color(0x20DCDFF6))
    ) {
        if (!isCardFocused){
            val (image, name, test) = createRefs()
            Image(
                painter = painterResource(id = card.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Column(modifier= Modifier.constrainAs(name){
                top.linkTo(image.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(test.top)
            },
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = card.title,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = card.description,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0x10FFFFFF)
                ),
                shape = RoundedCornerShape(7.dp),
                modifier = Modifier.constrainAs(test) {
                    top.linkTo(name.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end )
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Text("테스트 해보기",color= Color(0xB3FFFFFF), fontSize = 11.sp)
            }
        }else{
            val (image, name, content) = createRefs()

            Text(
                text = card.title,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(name){
                    bottom.linkTo(image.top)
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
            Image(
                painter = painterResource(id = card.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .constrainAs(image) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = "지금 ${card.title}을 해보세요.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(content){
                    top.linkTo(image.bottom, margin = 20.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        }


    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LUMOSTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.Transparent
        ) {
            GestureScreen()
        }
    }
}
