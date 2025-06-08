package com.hogumiwarts.lumos.presentation.ui.screens.devices.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DeviceIntent
import com.hogumiwarts.lumos.presentation.ui.viewmodel.DeviceViewModel
import com.hogumiwarts.lumos.presentation.ui.viewmodel.WebSocketViewModel


@Composable
fun WearIconButton(
    viewModel: DeviceViewModel = hiltViewModel(),
    webSocketViewModel: WebSocketViewModel = hiltViewModel()
                   ) {
    val interactionSource = remember { MutableInteractionSource() }
    Image(
        painter = painterResource(id = R.drawable.ic_refresh),
        contentDescription = "My Image Button",
        modifier = Modifier
            .size(54.dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    viewModel.sendIntent(DeviceIntent.Refresh)
                    webSocketViewModel.connectWebSocket()
                }
            )
            .padding(top = 24.dp)
    )
}