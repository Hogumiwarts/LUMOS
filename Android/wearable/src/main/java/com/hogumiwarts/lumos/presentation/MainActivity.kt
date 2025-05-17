/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.hogumiwarts.lumos.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.wearable.Wearable
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.theme.LUMOSTheme
import com.hogumiwarts.lumos.presentation.ui.navigation.NavGraph
import com.hogumiwarts.lumos.presentation.ui.screens.LoginScreen
import com.hogumiwarts.lumos.presentation.ui.screens.control.speaker.MoodPlayerScreen
import com.hogumiwarts.lumos.presentation.ui.screens.gesture.GestureTestScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        val text = intent.getStringExtra("text") ?: ""

        setTheme(android.R.style.Theme_DeviceDefault)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (checkSelfPermission(Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 1001)
            }
        }

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
                    if(text ==""){
                        NavGraph(navController)
//                        LoginScreen()
//                        MoodPlayerScreen(1L)
//                        LightOtherSetting()
                    }else{
                        GestureTestScreen(text) {
                            sendTextToMobile(this@MainActivity, it)
                        }


                    }

                }

            }


//            WearApp("Android")
        }
//        val intent = Intent(this, SensorService::class.java)
//        ContextCompat.startForegroundService(this, intent)
    }
}

fun sendTextToMobile(context: Context, message: String) {
    val messageClient = Wearable.getMessageClient(context)
    val path = "/watch_to_mobile_text"

    Wearable.getNodeClient(context).connectedNodes
        .addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("TAG", "sendTextToMobile: 보내기")
                messageClient.sendMessage(node.id, path, message.toByteArray())
            }
        }
}





@Preview(device = Devices.WEAR_OS_SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() {
    LUMOSTheme{
//        DevicesScreen()
    }
}