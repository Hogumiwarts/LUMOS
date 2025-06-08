package com.hogumiwarts.lumos.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Text
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.presentation.ui.viewmodel.DeviceViewModel
import com.hogumiwarts.lumos.presentation.ui.viewmodel.TokenState
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit, goLogin: ()->Unit, viewModel: DeviceViewModel = hiltViewModel(),){
//    LaunchedEffect(true) {
//        delay(3000)
//
////        onTimeout()
//    }
    val token by viewModel.tokenState.collectAsState()
    LaunchedEffect(token) {
        when(token){
            is TokenState.Loaded -> {
                if ((token as TokenState.Loaded).token==""){
                    Log.d("TAG", "SplashScreen: s$token")
                    goLogin()
                }else{
                    Log.d("TAG", "SplashScreen: $token")
                    onTimeout()
                }
            }
            TokenState.Loading -> {}
        }
    }


    LaunchedEffect(Unit) {

        Log.d("TAG", "SplashScreen: 시작")
       viewModel.getAccess() // 이 내부에서 viewModelScope.launch 사용 가능

    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,

    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_background),
            contentDescription = "설명",
            modifier = Modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "설명",
            modifier = Modifier.size(110.dp)
        )
    }
}

