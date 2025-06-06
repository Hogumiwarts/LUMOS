package com.hogumiwarts.lumos.ui.screens.control

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.hogumiwarts.domain.model.minibig.SwitchStatusData
import com.hogumiwarts.lumos.ui.screens.control.minibig.SwitchIntent
import com.hogumiwarts.lumos.ui.screens.control.minibig.SwitchPowerState
import com.hogumiwarts.lumos.ui.screens.control.minibig.SwitchStatusState
import com.hogumiwarts.lumos.ui.viewmodel.SwitchViewModel



@Composable
fun SwitchScreen(deviceId: Long, viewModel: SwitchViewModel = hiltViewModel()) {

    var checked by remember { mutableStateOf(true) }
    var name by remember { mutableStateOf("스위치") }
    var image by remember { mutableStateOf("") }
    var deviceModel by remember { mutableStateOf("") }
    var deviceType by remember { mutableStateOf("") }

// 최초 진입 시 상태 요청
    LaunchedEffect(Unit) {
        viewModel.sendIntent(SwitchIntent.LoadSwitchStatus(deviceId))
    }

    val state by viewModel.state.collectAsState()
    val powerState by viewModel.powerState.collectAsState()
    val isOn by viewModel.isOn.collectAsState()

    var switchDevice = remember {
        SwitchStatusData(
            tagNumber = 1,
            deviceId = 123,
            manufacturerCode = "MiniBig",
            deviceImg = "https://example.com/test.png",
            deviceName = "침대 조명 스위치",
            deviceType = "스위치",
            deviceModel = "Smart Switch v2",
            activated = true
        )
    }
    LaunchedEffect(state) {
        when (state) {
            is SwitchStatusState.Error -> {
                // TODO: 에러 처리
            }

            SwitchStatusState.Idle -> {}
            is SwitchStatusState.Loaded -> {
                val data = (state as SwitchStatusState.Loaded).data
                checked = data.activated
                switchDevice = data
                name = data.deviceName
                image = data.deviceImg?:""
                deviceModel = data.deviceModel
                deviceType = data.deviceType
            }

            SwitchStatusState.Loading -> {
                // TODO: 로딩 화면
            }
        }
    }
    when(powerState){
        is SwitchPowerState.Error -> {
            // TODO: 에러 처리
        }
        SwitchPowerState.Idle -> {}
        is SwitchPowerState.Loaded -> {
            checked = !checked
        }
        SwitchPowerState.Loading -> {
            // TODO: 로딩 화면
        }
    }
    // 더미

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(WindowInsets.statusBars.asPaddingValues().calculateTopPadding()))
        Text(
            text = name,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(41.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "스위치",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Switch(
                checked = isOn,
                onCheckedChange = {
                    viewModel.sendIntent(SwitchIntent.ChangeSwitchPower(deviceId, activated = it))
                    checked = it
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xff3E4784),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFB0B0B0)
                )
            )
        }

        Spacer(modifier = Modifier.height(17.dp))


        AsyncImage(
            model = image,
            contentDescription = null,
            modifier = Modifier.size(250.dp),
            contentScale = ContentScale.Fit
        )

//        Image(
//            painter = painterResource(id = R.drawable.ic_switch),
//            contentDescription = "스위치",
//            modifier = Modifier.size(250.dp)
//        )

        Spacer(modifier = Modifier.height(54.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                "기기 정보",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                "제조사 | ${switchDevice.manufacturerCode}",
                fontSize = 12.sp
            )

            Text(
                "모델명 | ${deviceModel}",
                fontSize = 12.sp
            )
            Text(
                "연결방식 | Wi-Fi",
                fontSize = 12.sp
            )
            Text(
                "기기 타입 | ${deviceType}",
                fontSize = 12.sp
            )

        }


    }
}

@Preview(showBackground = true)
@Composable
fun SwitchPreview() {
    SwitchScreen(1L)

}