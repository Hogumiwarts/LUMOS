package com.hogumiwarts.lumos.ui.screens.auth.signup

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.hogumiwarts.lumos.ui.viewmodel.AuthIntent
import com.hogumiwarts.lumos.ui.screens.auth.components.GradientButton
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginEffect
import com.hogumiwarts.lumos.ui.screens.auth.login.LoginIntent
import com.hogumiwarts.lumos.ui.theme.nanum_square_neo
import com.hogumiwarts.lumos.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(
    viewModel: SignupViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),

    onSignupSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                // 회원가입 성공하면 토스트 메시지 띄움
                SignupEffect.ShowSignupSuccessToast -> {
                    Toast.makeText(context, "가입을 축하드려요! LUMOS에 오신 걸 환영합니다 🪄", Toast.LENGTH_SHORT)
                        .show()
                }
                // 회원가입 성공하면 login 페이지로 이동
                SignupEffect.NavigateToLogin -> {
                    onSignupSuccess()
                }

                SignupEffect.SignupCompleted -> {
                    authViewModel.handleIntent(AuthIntent.SignUp)
                }
            }
        }
    }

    // 회원가입 UI
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
            painter = painterResource(id = R.drawable.bg_signup_space),
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
                    viewModel.handleIntent(SignupIntent.inputId(it))
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
                    viewModel.handleIntent(SignupIntent.inputPw(it))
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
                        viewModel.handleIntent(SignupIntent.togglePasswordVisibility)
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

            Spacer(modifier = Modifier.height(18.dp))

            // PW 입력
            OutlinedTextField(
                value = state.pw2,
                onValueChange = {
                    viewModel.handleIntent(SignupIntent.inputPw2(it))
                },
                isError = state.pw2ErrorMessage != null,
                placeholder = {
                    Text(
                        "PW 확인",
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
                        if (state.pw2ErrorMessage != null) Modifier
                            .border(1.5.dp, Color(0xFFF26D6D), shape = MaterialTheme.shapes.medium)
                        else Modifier
                    ),
                singleLine = true,
                visualTransformation = if (state.password2Visible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(SignupIntent.togglePassword2Visibility)
                    }) {
                        val iconRes =
                            if (state.password2Visible) R.drawable.ic_eye_on else R.drawable.ic_eye_off
                        Image(
                            painter = painterResource(id = iconRes),
                            modifier = Modifier.size(20.dp),
                            contentDescription = if (state.password2Visible) "비밀번호 숨기기" else "비밀번호 보기"
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

            state.pw2ErrorMessage?.let {
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

            // 이름 입력
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    viewModel.handleIntent(SignupIntent.inputName(it))
                },
                isError = state.nameErrorMessage != null,
                placeholder = {
                    Text(
                        "이름",
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
                        if (state.nameErrorMessage != null) Modifier
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

            state.nameErrorMessage?.let {
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

            // 회원가입 버튼
            GradientButton(
                onClick = { viewModel.handleIntent(SignupIntent.submitSignup(context)) },
                inputText = "회원가입"
            )

            Spacer(modifier = Modifier.height(80.dp))

        }
    }
}