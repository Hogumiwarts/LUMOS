package com.hogumiwarts.data.repository

import com.hogumiwarts.data.source.local.JwtLocalDataSource
import com.hogumiwarts.domain.repository.JwtRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class JwtRepositoryImpl @Inject constructor(
    private val localDataSource: JwtLocalDataSource
) : JwtRepository {
    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        localDataSource.saveTokens(accessToken, refreshToken)
    }

    override fun getAccessToken(): Flow<String> = localDataSource.getAccessToken()

    override fun getRefreshToken(): Flow<String> = localDataSource.getRefreshToken()

    override suspend fun clearTokens() {
        localDataSource.clearTokens()
    }
}