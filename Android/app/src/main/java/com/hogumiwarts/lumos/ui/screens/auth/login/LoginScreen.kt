package com.hogumiwarts.lumos.ui.screens.auth.login

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.screens.auth.components.GradientButton
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),

    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToHome -> {
                    onLoginSuccess()
                    authViewModel.logIn()
                }
                LoginEffect.ShowWelcomeToast ->
                    Toast.makeText(context, "LUMOS에 오신 걸 환영해요!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1C3A), Color(0xFF251744))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 배경 이미지
        Image(
            painter = painterResource(id = R.drawable.bg_login_space),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.FillWidth
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(121.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "LUMOS",
                    style = TextStyle(
                        fontSize = 60.sp,
                        fontFamily = FontFamily(Font(R.font.crimsontext_regular)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFFFFFFFF),
                        textAlign = TextAlign.Center,
                    )
                )


                Text(
                    text = "손 끝으로 제어하는 나만의 스마트 홈",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = nanum_square_neo,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // 로그인 영역
            // ID 입력
            OutlinedTextField(
                value = state.id,
                onValueChange = {
                    viewModel.handleIntent(LoginIntent.inputId(it))
                },
                isError = state.idErrorMessage != null,
                placeholder = {
                    Text(
                        "ID",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA1A1A1)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .then(
                        if (state.idErrorMessage != null) Modifier
                            .border(1.5.dp, Color(0xFFF26D6D), shape = MaterialTheme.shapes.medium)
                        else Modifier
                    ),
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    errorBorderColor = Color.Transparent,      // 중복 방지
                    errorCursorColor = Color(0xFFF26D6D),
                    errorTextColor = Color(0xFFF26D6D),

                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                )
            )

            state.idErrorMessage?.let {
                Text(
                    text = it,
                    color = Color(0xFFF26D6D),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )
            }


            Spacer(modifier = Modifier.height(18.dp))

            // PW 입력
            OutlinedTextField(
                value = state.pw,
                onValueChange = {
                    viewModel.handleIntent(LoginIntent.inputPw(it))
                },
                isError = state.pwErrorMessage != null,
                placeholder = {
                    Text(
                        "PW",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = nanum_square_neo,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA1A1A1)
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .then(
                        if (state.pwErrorMessage != null) Modifier
                            .border(1.5.dp, Color(0xFFF26D6D), shape = MaterialTheme.shapes.medium)
                        else Modifier
                    ),
                singleLine = true,
                visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(LoginIntent.togglePasswordVisibility)
                    }) {
                        val iconRes =
                            if (state.passwordVisible) R.drawable.ic_eye_on else R.drawable.ic_eye_off
                        Image(
                            painter = painterResource(id = iconRes),
                            modifier = Modifier.size(20.dp),
                            contentDescription = if (state.passwordVisible) "비밀번호 숨기기" else "비밀번호 보기"
                        )
                    }
                },
                shape = MaterialTheme.shapes.medium,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    errorBorderColor = Color.Transparent,      // 중복 방지
                    errorCursorColor = Color(0xFFF26D6D),
                    errorTextColor = Color(0xFFF26D6D),

                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                )
            )

            state.pwErrorMessage?.let {
                Text(
                    text = it,
                    color = Color(0xFFF26D6D),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(52.dp))

            // 로그인 버튼
            GradientButton(
                onClick = { viewModel.handleIntent(LoginIntent.submitLogin) },
                inputText = "로그인"
            )

            Spacer(modifier = Modifier.height(80.dp))

        }
    }
}
