package com.hogumiwarts.myapplication.presentation.ui

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun ShowPredictionToast(prediction: String) {
    val context = LocalContext.current
    Toast.makeText(context, "📨 예측: $prediction", Toast.LENGTH_SHORT).show()
}
