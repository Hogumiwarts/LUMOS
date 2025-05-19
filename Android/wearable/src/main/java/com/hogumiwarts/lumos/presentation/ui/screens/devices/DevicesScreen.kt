// 필요한 컴포즈 및 리소스 관련 import
package com.hogumiwarts.lumos.presentation.ui.screens.devices

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme

//

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults
import com.google.android.horologist.compose.layout.ScalingLazyColumnDefaults.ItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnState
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.lumos.presentation.ui.common.ErrorMessage
import com.hogumiwarts.lumos.presentation.ui.screens.devices.components.LoadedDevice
import com.hogumiwarts.lumos.presentation.ui.screens.devices.components.LoadingDevice
import com.hogumiwarts.lumos.presentation.ui.viewmodel.DeviceViewModel

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun DevicesScreen(
    navController: NavHostController,
    viewModel: DeviceViewModel = hiltViewModel(),
) {

    // 최초 진입 시 DeviceIntent 전송
    LaunchedEffect(Unit) {
        Log.d("TAG", "DevicesScreen: 호출")
//        viewModel.saveJwt("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3MDM1MDI0LCJleHAiOjE3NDcxMjE0MjR9.fZSp8dEpCWN-k1bB2zF_IEVn1Yi7_lIeev_zTJERnqY","eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3Mjg5MDAzLCJleHAiOjE3NDc4OTM4MDN9.XLnwDciZxOjolAJfpM1Ej7a_UNB9-kRphbvZL5RIOHo")
        viewModel.sendIntent(DeviceIntent.LoadDevice)
    }


    // 상태 관찰
    val state by viewModel.state.collectAsState()



    // Horologist의 ScalingLazyColumn 스크롤 상태 구성
    val listState = rememberResponsiveColumnState(
        contentPadding = ScalingLazyColumnDefaults.padding(
            first = ItemType.SingleButton,
            last = ItemType.Unspecified,
        ),
    )

    when (state) {
        DeviceState.Idle -> {
            // 초기 상태 처리 (필요 없으면 생략 가능)
        }

        DeviceState.Loading -> {
            LoadingDevice() // 로딩 중 UI
        }

        is DeviceState.Loaded -> {
            LoadedDevice(
                devices = (state as DeviceState.Loaded).data,
                listState = listState,
                navController = navController
            )
        }

        is DeviceState.Error -> {
            // 에러 UI 표시 또는 Snackbar 등으로 대응
            // 예시: 에러에 따라 구분해서 메시지 출력
            when ((state as DeviceState.Error).error) {
                CommonError.NetworkError -> {
                    // 네트워크 에러 UI
                    ErrorMessage("인터넷 연결을 확인해주세요.")
                }
                CommonError.UserNotFound -> {
                    ErrorMessage("사용자를 찾을 수 없습니다.")
                }
                else -> {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true } // splash를 백스택에서 제거
                    }
                }
            }
        }
    }

}

// 외곽선 그라디언트 브러시 정의
val gradientBrush = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFD1D5E9),
        Color(0xFF9DA6D0),
        Color(0xFF737FBC),
        Color(0xFF4B5BA9),
    )
)

enum class DeviceType(val type: String) {
    LIGHT("LIGHT"), SPEAKER("AUDIO"), MINIBIG("SWITCH"), AIR_PURIFIER("AIRPURIFIER");

    companion object {
        fun fromId(type: String): DeviceType? = values().find { it.type == type }
    }
}

// 프리뷰 (Wear OS 장치에서 시스템 UI 포함 화면 미리보기)
@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme {
//        DevicesScreen()
    }
}
