package com.hogumiwarts.lumos.ui.screens.Control

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.uwb.UwbAddress
import androidx.navigation.NavController
import com.hogumiwarts.lumos.ui.common.CommonTopBar
import com.hogumiwarts.lumos.utils.uwb.UwbRangingManager
import com.hogumiwarts.lumos.utils.uwb.BleScanner
import com.hogumiwarts.lumos.utils.uwb.GattConnector
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin


@Composable
fun ControlScreen(navController: NavController) {

    Scaffold(
        topBar = {
            CommonTopBar(
                barTitle = "SmartTag2 제어",
                onBackClick = {
                    navController.popBackStack()
                },
                isAddBtnVisible = false,
                onAddClick = {})
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.Gray)
        ) {

        }
    }
}


@Composable
fun RoleSelector(onPick: (Role) -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Choose Device Role", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(40.dp))
        Button(
            onClick = { onPick(Role.CONTROLLER) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("I am the Controller") }
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = { onPick(Role.CONTROLEE) },
            modifier = Modifier.fillMaxWidth()
        ) { Text("I am a Controlee (IoT anchor)") }
    }
}

enum class Role { CONTROLLER, CONTROLEE }