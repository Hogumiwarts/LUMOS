package com.hogumiwarts.lumos.ui.screens.auth.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.PrimaryButton
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlinx.coroutines.launch
import mx.platacard.pagerindicator.PagerIndicator

// 온보딩 페이지 데이터 클래스
data class OnboardingPage(
    val image: Int,
    val title: String,
    val description: String
)

@Composable
fun OnBoardingScreen(
    onFinish: () -> Unit = {}
) {

    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    // 온보딩 페이지 내용
    val onboardingPages = listOf(
        OnboardingPage(
            image = R.drawable.onboarding_1,
            title = "방향 인식으로 더 스마트한 제어",
            description = "기기를 향해 스마트폰을 들기만 만해도\n자동으로 연결하고 제어해요"
        ),
        OnboardingPage(
            image = R.drawable.onboarding_2,
            title = "손짓 한 번으로 자동 제어",
            description = "자주 사용하는 동작을 저장하고\n한 번의 제스처로 실행하세요"
        ),
        OnboardingPage(
            image = R.drawable.onboarding_3,
            title = "사용자 맞춤형 루틴 설정",
            description = "좋아하는 조명 색, 음악, 공기 청정기 팬 세기까지\n내 마음대로 설정하세요"
        ),
        OnboardingPage(
            image = R.drawable.onboarding_4,
            title = "실시간 기기 상태 확인",
            description = "앱에서 연결된 기기의 상태를 실시간으로 확인하고\n언제든 변경할 수 있어요"
        ),
    )

    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_onboardings),
            contentDescription = "온보딩 페이지 배경",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 32.dp,
                    end = 32.dp,
                    top = 32.dp,
                    bottom = 32.dp + navBarHeight
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.weight(0.1f))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.8f)
            ) { page ->
                OnboardingPageContent(onboardingPages[page])
            }

            Spacer(modifier = Modifier.weight(0.1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                PagerIndicator(
                    pagerState = pagerState,
                    activeDotColor = Color(0xff4B5BA9),
                    dotColor = Color.LightGray,
                    dotCount = onboardingPages.size,
                    activeDotSize = 8.dp,
                    normalDotSize = 6.dp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            PrimaryButton(
                buttonText = if (pagerState.currentPage == onboardingPages.size - 1) "시작하기" else "다음",
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

    }

}


@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        Image(
            painter = painterResource(id = R.drawable.onboarding_icon),
            modifier = Modifier.size(100.dp),
            contentDescription = "온보딩 아이콘"
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = page.title,
            fontFamily = nanum_square_neo,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
//            Box(
//                modifier = Modifier
//                    .align(Alignment.Center)
//                    .clip(RoundedCornerShape(50.dp))
//                    .background(Color.White.copy(alpha = 0.1f))
//            ) {

            Image(
                painter = painterResource(id = page.image),
                contentDescription = page.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
//            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = page.description,
            fontFamily = nanum_square_neo,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun pr() {
    OnBoardingScreen({})
}