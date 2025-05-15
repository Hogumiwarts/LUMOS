package com.hogumiwarts.lumos.di

import com.hogumiwarts.lumos.DataStore.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAuthInterceptor @Inject constructor(
    private val tokenDataStore: TokenDataStore
) : Interceptor {

    private val excludedPaths = listOf(
        "/api/sso/refresh",
        "/api/sso/login"
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()

        val isExcludedPath = excludedPaths.any { path -> requestUrl.contains(path) }
        if (isExcludedPath) return chain.proceed(originalRequest)

        // 최신 토큰 가져오기
        val accessToken = runBlocking {
            tokenDataStore.getAccessToken().first()
        }

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $accessToken")
            .build()

        return chain.proceed(newRequest)
    }
}
