package com.hogumiwarts.lumos.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo

@Composable
fun CommonDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    titleText: String,
    bodyText: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                PrimaryButton(
                    buttonText = "확인",
                    onClick = onDismiss
                )
            },
            title = {
                Text(
                    text = titleText,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(800),
                    color = Color(0xFF000000),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Text(
                    text = bodyText,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontFamily = nanum_square_neo,
                    fontWeight = FontWeight(400),
                    color = Color(0x80151920),
                    textAlign = TextAlign.Center
                )
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
