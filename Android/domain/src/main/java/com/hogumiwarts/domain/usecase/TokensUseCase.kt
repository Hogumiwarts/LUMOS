package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.repository.JwtRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TokensUseCase @Inject constructor(
    private val repository: JwtRepository
) {
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        repository.saveTokens(accessToken, refreshToken)
    }

    fun getAccessToken (): Flow<String> = repository.getAccessToken()

    fun getRefreshToken (): Flow<String> = repository.getRefreshToken()

    suspend fun clearTokens  () {
        repository.clearTokens()
    }
}