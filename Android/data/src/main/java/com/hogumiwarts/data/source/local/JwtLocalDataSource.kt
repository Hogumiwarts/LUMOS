package com.hogumiwarts.data.source.local

import kotlinx.coroutines.flow.Flow

interface JwtLocalDataSource {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    fun getAccessToken(): Flow<String>
    fun getRefreshToken(): Flow<String>
    suspend fun clearTokens()
}