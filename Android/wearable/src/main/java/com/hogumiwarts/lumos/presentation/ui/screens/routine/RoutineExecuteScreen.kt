package com.hogumiwarts.lumos.presentation.ui.screens.routine

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.hogumiwarts.lumos.R
import org.jetbrains.annotations.Async

@Composable
fun RoutineExecuteScreen() {


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 배경
        Image(
            painter = painterResource(id = R.drawable.device_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
        )

        Column {
//            AsyncImage(model = , contentDescription = )
        }
    }

}


@Composable
@Preview(showBackground = true, device = Devices.WEAR_OS_SMALL_ROUND)
fun RoutineExecuteScreenPreview() {
    RoutineExecuteScreen()
}