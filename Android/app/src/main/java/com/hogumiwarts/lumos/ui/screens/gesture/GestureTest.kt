package com.hogumiwarts.lumos.ui.screens.gesture

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.lumos.GestureTestViewModel
import com.hogumiwarts.lumos.ui.common.CommonDialog
import com.hogumiwarts.lumos.ui.screens.gesture.components.GestureTestCard
import timber.log.Timber
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import kotlin.math.abs
import com.hogumiwarts.lumos.ui.screens.gesture.components.CustomPagerIndicator

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun GestureTest(
    cards: List<GestureData>,
    onGestureSelected: (GestureData) -> Unit
) {
    var showAlreadySelectedDialog by remember { mutableStateOf(false) }

    if (showAlreadySelectedDialog) {
        CommonDialog(
            showDialog = true,
            onDismiss = { showAlreadySelectedDialog = false },
            titleText = "이미 연결된 제스처예요",
            bodyText = "다른 제스처를 선택하거나, 기존 루틴을 먼저 해제해주세요."
        )
    }

    LaunchedEffect(cards) {
        Timber.tag("gesture").d("📌 GestureTest 전달받은 카드 수: ${cards.size}")
    }

    val viewModel: GestureTestViewModel = viewModel()
    // 무한 스크롤 가능한 Pager 상태 설정
    val pagerState = rememberPagerState(
        initialPage = Int.MAX_VALUE / 2,
        pageCount = { Int.MAX_VALUE }
    )

    val selectedGesture = cards[pagerState.currentPage % cards.size] // 현재 선택된 제스처 인덱스를 계산

    // 카드가 선택된 상태를 저장하는 상태 변수
    var isCardFocused by remember { mutableStateOf(false) }

    // 화면 전체를 제약 조건으로 구성
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (title, card, select, indicator) = createRefs()

        // 상단 제목 및 설명 영역
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(title) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top, margin = 90.dp)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isCardFocused) "제스처 테스트" else "제스처 선택",
                style = TextStyle(
                    fontSize = 22.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(800),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                )
            )

            Spacer(modifier = Modifier.size(13.dp))

            Text(
                text = if (isCardFocused)
                    "선택 전에 직접 제스처를 체험해보세요."
                else
                    "원하는 제스처를 선택해 기기를 제어하세요.",
                style = TextStyle(
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0xFFFFFFFF),
                    textAlign = TextAlign.Center,
                )
            )
        }

        // 패딩과 간격 애니메이션 (선택 시 변경)
        val animatedPadding by animateDpAsState(
            targetValue = if (isCardFocused) 40.dp else 50.dp,
            animationSpec = tween(durationMillis = 300),
            label = "paddingAnimation"
        )
        val animatedSpacing by animateDpAsState(
            targetValue = if (isCardFocused) 0.dp else (-25).dp,
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
                top.linkTo(title.bottom, margin = 25.dp)
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

        CustomPagerIndicator(
            currentPage = pagerState.currentPage % cards.size,
            pageCount = cards.size,
            modifier = Modifier
                .constrainAs(indicator) {
                    top.linkTo(card.bottom, margin = 10.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )


        // 하단 "선택하기" 버튼 (카드 선택 전만 표시)
        if (!isCardFocused) {
            val isSelectable = selectedGesture.routineName.isBlank() // 해당 루틴이 이미 선택되었는지 여부 판단

            Button(
                onClick = {
                    if (isSelectable) {
                        onGestureSelected(selectedGesture)
                    } else {
                        showAlreadySelectedDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff3E4784).copy(alpha = if (isSelectable) 1f else 0.5f),
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .constrainAs(select) {
                        top.linkTo(card.bottom, margin = 60.dp)
                        start.linkTo(parent.start, margin = 30.dp)
                        end.linkTo(parent.end, margin = 30.dp)
                        width = Dimension.fillToConstraints
                    }
                    .height(45.dp)
                    .then(
                        if (!isSelectable) Modifier.border(
                            width = 1.dp,
                            color = Color(0xA04C4C4C),
                            shape = RoundedCornerShape(10.dp)
                        ) else Modifier
                    )
            ) {
                Text(
                    text = if (isSelectable) "선택하기" else "🔒 연결된 제스처",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 16.sp,
                        fontFamily = nanum_square_neo,
                        fontWeight = FontWeight(800),
                        color = Color.White.copy(alpha = if (isSelectable) 1f else 0.4f),
                        textAlign = TextAlign.Center,
                    )
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.S)
@Composable
@Preview(showBackground = true)
fun GestureTestPreview() {
    val sampleGestures = listOf(
        GestureData(
            gestureId = 1,
            gestureName = "두 번 박수",
            gestureDescription = "가슴 앞에서 두 번 박수칩니다",
            gestureImageUrl = "https://example.com/sample1.png",
            routineName = "",
            routineId = 1
        ),
        GestureData(
            gestureId = 2,
            gestureName = "팔 올리기",
            gestureDescription = "두 팔을 천천히 위로 들어올립니다",
            gestureImageUrl = "https://example.com/sample2.png",
            routineName = "취침",
            routineId = 2
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111A3A)) // 진한 남색 배경
    ) {
        GestureTest(cards = sampleGestures, onGestureSelected = {})
    }
}

