package com.hogumiwarts.lumos.presentation.ui.screens.control.minibig

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.common.AnimatedToggleButton
import com.hogumiwarts.lumos.presentation.ui.common.ErrorMessage
import com.hogumiwarts.lumos.presentation.ui.screens.control.ControlState
import com.hogumiwarts.lumos.presentation.ui.screens.devices.components.LoadingDevice
import com.hogumiwarts.lumos.presentation.ui.viewmodel.SwitchViewModel

// 🟢 최상위 Composable - 스크린 전체를 구성
@Composable
fun SwitchScreen(
    deviceId: Long,
    viewModel: SwitchViewModel = hiltViewModel()
) {
    // 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.sendIntent(SwitchIntent.LoadSwitchStatus(deviceId))
    }

    // ViewModel 상태 수신
    val state by viewModel.state.collectAsState()
    val powerState by viewModel.powerState.collectAsState()
    val isOn by viewModel.isOn.collectAsState() // isOn 상태를 ViewModel에서 별도로 관리 중

    // 상태 분기 처리
    when (state) {
        is SwitchStatusState.Error -> {
            when ((state as SwitchStatusState.Error).error) {
                CommonError.NetworkError -> ErrorMessage("인터넷 연결을 확인해주세요.")
                CommonError.UserNotFound -> ErrorMessage("사용자를 찾을 수 없습니다.")
                else -> ErrorMessage("알 수 없는 오류가 발생했습니다.")
            }
        }

        SwitchStatusState.Idle -> Unit

        is SwitchStatusState.Loaded -> {
            val data = (state as SwitchStatusState.Loaded).data

            Log.d("TAG", "SwitchScreen: $isOn") // 현재 상태 로그 출력

            BedLightSwitch(
                isChecked = isOn,
                onClick = {
                    viewModel.sendIntent(SwitchIntent.ChangeSwitchPower(deviceId, !isOn))
                },
                name = data.deviceName,
                deviceId = deviceId
            )
        }

        SwitchStatusState.Loading -> {
            LoadingDevice()
        }
    }

    // 전환 중 로딩 표시
    when (powerState) {
        is ControlState.Error -> Unit
        ControlState.Idle -> Unit
        is ControlState.Loaded -> Unit // 상태 전환 후 별도 처리 없음
        ControlState.Loading -> {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}


// 🟡 UI 구성 (텍스트 + 토글 + 하단 안내 포함)
@Composable
fun BedLightSwitch(
    isChecked: Boolean,
    onClick: () -> Unit,
    name:String,
    deviceId: Long
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
            text = name,
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
            onToggle = { onClick() },
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
                    bottom.linkTo(parent.bottom, margin = 10.dp)
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
        SwitchScreen(1L)
    }
}
