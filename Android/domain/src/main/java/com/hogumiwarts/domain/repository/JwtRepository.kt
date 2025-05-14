package com.hogumiwarts.domain.repository

import kotlinx.coroutines.flow.Flow

interface JwtRepository {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): Flow<String>
    fun getRefreshToken(): Flow<String>
    suspend fun clearTokens()
}