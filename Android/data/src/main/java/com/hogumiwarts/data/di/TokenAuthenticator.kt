package com.hogumiwarts.data.di

import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.data.token.TokenStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named

class TokenAuthenticator @Inject constructor(
    private val tokenDataStore: TokenStorage,
    @Named("refresh") private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null // 무한루프 방지

        return try {
            runBlocking {
                val refreshToken = tokenDataStore.getRefreshToken().first()

                val refreshResponse = authApi.refresh("Bearer $refreshToken")
                val newAccessToken = refreshResponse.data.accessToken

                tokenDataStore.saveTokens(
                    accessToken = newAccessToken,
                    refreshToken = refreshToken,
                    name = tokenDataStore.getUserName().first()
                )

                response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
