package com.hogumiwarts.lumos.di

import android.content.Context
import android.util.Log
import com.hogumiwarts.domain.usecase.TokensUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

// ✅ Hilt DI를 통해 싱글톤으로 제공되는 인증 인터셉터
@Singleton
class AddAuthInterceptor @Inject constructor(
    @ApplicationContext private val context: Context, // 앱 전체 context 주입
    private val jwtUseCase: TokensUseCase,
) : Interceptor {

    // 🔹 인증이 필요 없는 예외 API 경로 리스트
    private val excludedPaths = listOf(
        "/api/sso/refresh",
        "/api/sso/login"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()

        // 🔸 요청 URL이 예외 경로에 포함되는지 확인
        val isExcludedPath = excludedPaths.any { path ->
            requestUrl.contains(path)
        }

        // 🔸 예외 경로면 토큰 없이 그대로 요청
        if (isExcludedPath) {
            return chain.proceed(originalRequest)
        }
        val accessToken = runBlocking {
            jwtUseCase.getAccessToken().first()
        }

        Log.d("TAG", "intercept: $accessToken")

        // 🔸 예외가 아닌 경우 → 토큰을 헤더에 추가
//        val tokenManager = TokenManager(context)
//        val accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzQ3Mzc4MTEwLCJleHAiOjE3NDc0NjQ1MTB9.sdOs-PHeJjeQOEhrzAqA-ldDVMbzZqgHjo3rIlsVypw" // 🔺 실제로는 TokenManager 등에서 토큰 받아와야 함

        // 🔹 토큰이 존재하면 Authorization 헤더 추가
        val newRequest = if (accessToken?.isNotEmpty() == true) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(newRequest)
    }
}
