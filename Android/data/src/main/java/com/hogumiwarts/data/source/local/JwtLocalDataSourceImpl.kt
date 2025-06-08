package com.hogumiwarts.data.source.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JwtLocalDataSourceImpl @Inject constructor(
    private val jwtDataStore: JwtDataStore
) : JwtLocalDataSource {
    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        jwtDataStore.saveTokens(accessToken, refreshToken)
    }

    override fun getAccessToken(): Flow<String> = jwtDataStore.getAccessToken()

    override fun getRefreshToken(): Flow<String> = jwtDataStore.getRefreshToken()

    override suspend fun clearTokens() {
        jwtDataStore.clearTokens()
    }
}