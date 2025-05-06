package com.hogumiwarts.lumos.presentation.ui.screens.control.minibig

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.common.AnimatedToggleButton

// 🟢 최상위 Composable - 스크린 전체를 구성
@Composable
fun MinibigScreen(tagNumber: Long) {
    var isOn by remember { mutableStateOf(false) } // 전체 스위치 상태
    BedLightSwitch(
        isChecked = isOn,
        onCheckedChange = { isOn = it }
    )
}

// 🟡 UI 구성 (텍스트 + 토글 + 하단 안내 포함)
@Composable
fun BedLightSwitch(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
    )  {

        Image(
            painter = painterResource(id = R.drawable.device_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )
        val (title, toggle, arrow) = createRefs()
        // 상단 텍스트
        Text(
            text = "침대 조명 스위치",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top, margin = 18.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(toggle.top)
            }
        )


        // 🟠 내부에서 별도 토글 상태 선언 → 외부 isChecked와 동기화되지 않음 (주의 필요)
        AnimatedToggleButton(
            isOn = isChecked,
            onToggle = { onCheckedChange(it) },
            modifier = Modifier.constrainAs(toggle) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )


        // 하단 안내 텍스트
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0x10FFFFFF),
            modifier = Modifier
                .constrainAs(arrow) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(toggle.bottom)
                }
        ) {
            Text(
                text = "폰에서 세부 제어",
                color = Color.White,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                style = TextStyle(fontSize = 14.sp)
            )
        }

    }
}

// 🧪 Wear OS 에뮬레이터에서 미리보기 지원
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
        MinibigScreen(1L)
    }
}
