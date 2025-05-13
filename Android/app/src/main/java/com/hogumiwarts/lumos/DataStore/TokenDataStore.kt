package com.hogumiwarts.lumos.DataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hogumiwarts.lumos.di.BaseUrlModule.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// hilt 방식으로 변경
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    val accessTokenFlow: Flow<String> = context.dataStore.data
        .map { it[ACCESS_TOKEN_KEY] ?: "" }

    // 키 정의
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

    // 저장 함수
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // AccessToken 가져오기
    fun getAccessToken(): Flow<String> {
        return context.dataStore.data.map { it[ACCESS_TOKEN_KEY] ?: "" }
    }

    // RefreshToken 가져오기
    fun getRefreshToken(): Flow<String> {
        return context.dataStore.data.map { it[REFRESH_TOKEN_KEY] ?: "" }
    }

    // 삭제 함수 (선택)
    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }
}