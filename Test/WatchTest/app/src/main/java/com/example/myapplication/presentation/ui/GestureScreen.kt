package com.example.myapplication.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Text


@Composable
fun GestureScreen(navController: NavController) {
    val motionLabels = listOf("모션1", "모션2", "모션3", "모션4") // ✨ 버튼 이름 리스트

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // 버튼 2개씩 묶어서 2줄 만들기
        for (rowItems in motionLabels.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach  {  label ->
                    val name  = when(label){
                        "모션2" -> 2
                        "모션3" -> 3
                        "모션4" -> 4
                        else -> 1
                    }
                    Button(
                        onClick = { navController.navigate("gesture_test/${name}"){
                            popUpTo("gesture_screen")
                        } },
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                    ) {
                        Text(text = label)
                    }
                }
                // 만약 한 줄에 버튼이 하나만 있으면 빈 공간 채우기
                if (rowItems.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun PreviewGestureScreen() {
//    GestureScreen()
}
