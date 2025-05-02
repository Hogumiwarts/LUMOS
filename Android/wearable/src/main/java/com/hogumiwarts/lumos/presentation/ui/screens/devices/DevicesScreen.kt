package com.hogumiwarts.lumos.presentation.ui.screens.devices

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
data class DeviceItem(val name: String, val status: Boolean, val icon: ImageVector)
@Composable
fun DevicesScreen(){

}


@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme{
        DevicesScreen()
    }
}