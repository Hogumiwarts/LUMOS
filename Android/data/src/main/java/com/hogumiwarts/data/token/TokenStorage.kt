package com.hogumiwarts.data.token

import kotlinx.coroutines.flow.Flow

interface TokenStorage {
    suspend fun getAccessToken(): Flow<String>
    suspend fun getRefreshToken(): Flow<String>
    suspend fun getUserName(): Flow<String>
    suspend fun saveTokens(accessToken: String, refreshToken: String, name: String)
    suspend fun clearTokens()
}