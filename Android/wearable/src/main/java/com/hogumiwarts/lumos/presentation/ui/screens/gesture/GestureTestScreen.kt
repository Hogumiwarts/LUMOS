package com.hogumiwarts.lumos.presentation.ui.screens.gesture

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.service.SensorService
import kotlin.math.log

@Composable
fun GestureTestScreen(
    type:String,
    onFinish: (String) -> Unit
) {

    val context = LocalContext.current
    var detectedGesture by remember { mutableStateOf<String?>(null) }
    val message = detectedGesture == "motion$type"

    Log.d("TAG", "detectedGesture : $detectedGesture, type : $type")

    // 서비스 시작 (센서 수집 및 추론 루프 실행)
    LaunchedEffect(Unit) {
        val intent = Intent(context, SensorService::class.java)
        intent.putExtra("isTest", true)
        ContextCompat.startForegroundService(context,intent)
        }

    DisposableEffect(Unit) {
        val appContext = context.applicationContext
        Log.d("TAG", "👉 리시버 등록 시도")

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val gesture = intent?.getStringExtra("gesture")
                Log.d("TAG", "onReceive: $gesture")
                detectedGesture = gesture

            }
        }

        val filter = IntentFilter("GESTURE_RESULT")
        ContextCompat.registerReceiver(
            appContext,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        Log.d("TAG", "✅ 리시버 등록 완료")

        onDispose {
            Log.d("TAG", "❌ 리시버 해제")
            appContext.unregisterReceiver(receiver)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1021)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 상태 텍스트
            if(message){
                Text(
                    text = "인식완료",
                    fontSize = 11.sp,
                    color = Color.Green
                )
                val intent = Intent(context, SensorService::class.java)
                context.stopService(intent)
            }else if(detectedGesture==null){

                Text(
                    text = "인식 중...",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
            }else{
                Text(
                    text = "인식 실패",
                    fontSize = 11.sp,
                    color = Color.Red
                )
                val intent = Intent(context, SensorService::class.java)
                context.stopService(intent)
            }

            Spacer(modifier = Modifier.size(8.dp))
            // 안내 문구
            Text(
                text = "제스처를 실행해보세요.",
                fontSize = 15.sp,
                color = Color.White
            )

            // 제스처 이모지
            Image(
                painter = painterResource(id = R.drawable.ic_motion1),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
            )
            // 완료 버튼
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF3A3A3C))
                    .clickable {
                        onFinish(if (message) "done" else "fail")
                        (context as? Activity)?.finish()
                    }
                    .padding(horizontal = 33.dp, vertical = 11.dp)
            ) {
                Text(
                    text = "완료하기",
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
    }
}



@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme{
//        GestureTestScreen({})
    }
}