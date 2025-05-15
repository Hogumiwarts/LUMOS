package com.hogumiwarts.lumos.presentation.ui.screens.devices.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text

// 리스트 중간에 구분선과 라벨 텍스트 표시용
@Composable
fun DividerWithLabel(label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 12.dp)
    ) {
        Divider(modifier = Modifier.weight(1f), color = Color.Gray)
        Text(
            text = label,
            color = Color(0xFFD5D5D5),
            modifier = Modifier.padding(horizontal = 8.dp),
            fontSize = 13.sp
        )
        Divider(modifier = Modifier.weight(1f), color = Color.Gray)
    }
    Spacer(modifier = Modifier.height(12.dp))
}