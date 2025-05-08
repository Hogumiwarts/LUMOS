/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.hogumiwarts.lumos.presentation

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.screens.control.minibig.MinibigScreen
import com.hogumiwarts.lumos.presentation.ui.screens.devices.DevicesScreen
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.navigation.NavGraph
import com.hogumiwarts.lumos.presentation.ui.screens.control.light.LightScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.MoodPlayerScreen
import com.hogumiwarts.lumos.service.SensorService

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            LUMOSTheme {
                val brightness = remember { mutableStateOf(1f) } // 초기 밝기 100%
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                ) {

                    Image(
                        painter = painterResource(id = R.drawable.device_background),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                    val navController = rememberNavController()
                    NavGraph(navController)
                }
            }


//            WearApp("Android")
        }
        val intent = Intent(this, SensorService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }
}





@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme{
//        DevicesScreen()
    }
}