package com.hogumiwarts.lumos.presentation.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.hogumiwarts.data.source.local.dataStore
import com.hogumiwarts.data.token.TokenStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// hilt 방식으로 변경
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context
): TokenStorage {
    val accessTokenFlow: Flow<String> = context.dataStore.data
        .map { it[ACCESS_TOKEN_KEY] ?: "" }

    // 키 정의
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    private val USER_NAME_KEY = stringPreferencesKey("user_name")

    // 저장 함수
    override suspend fun saveTokens(accessToken: String, refreshToken: String, name: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
            it[USER_NAME_KEY] = name
        }
    }

    override suspend fun saveAccessTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // AccessToken 가져오기
    override suspend fun getAccessToken(): Flow<String> {
        return context.dataStore.data.map { it[ACCESS_TOKEN_KEY] ?: "" }
    }

    // RefreshToken 가져오기
    override suspend fun getRefreshToken(): Flow<String> {
        return context.dataStore.data.map { it[REFRESH_TOKEN_KEY] ?: "" }
    }

    // 삭제 함수 (선택)
    override suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }

    // 사용자 이름 가져오기
    override suspend fun getUserName(): Flow<String> = context.dataStore.data.map { it[USER_NAME_KEY] ?: "" }


    // smartthings 관련
    private val INSTALLED_APP_ID = stringPreferencesKey("installed_app_id")
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")

    suspend fun saveSmartThingsTokens(installedAppId: String, authToken: String, name: String) {
        context.dataStore.edit {
            it[INSTALLED_APP_ID] = installedAppId
            it[AUTH_TOKEN] = authToken
            it[USER_NAME_KEY] = name
        }
    }

    suspend fun getInstalledAppId(): Flow<String> {
        return context.dataStore.data.map { it[INSTALLED_APP_ID] ?: "" }
    }

    suspend fun getSmartThingsAuthToken(): Flow<String> {
        return context.dataStore.data.map { it[AUTH_TOKEN] ?: "" }
    }

}