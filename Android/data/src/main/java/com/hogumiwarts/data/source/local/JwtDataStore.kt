package com.hogumiwarts.data.source.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// ✅ Context 확장 프로퍼티 (반드시 전역에 선언되어 있어야 함)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jwt_prefs")

class JwtDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // ✅ 토큰 저장
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.dataStore.edit {
            it[ACCESS_TOKEN_KEY] = accessToken
            it[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // ✅ 토큰 가져오기
    fun getAccessToken(): Flow<String> =
        context.dataStore.data.map { it[ACCESS_TOKEN_KEY] ?: "" }

    fun getRefreshToken(): Flow<String> =
        context.dataStore.data.map { it[REFRESH_TOKEN_KEY] ?: "" }

    // ✅ 전체 삭제
    suspend fun clearTokens() {
        context.dataStore.edit { it.clear() }
    }

    // ✅ SmartThings 관련 저장
    suspend fun saveSmartThingsTokens(installedAppId: String, authToken: String) {
        context.dataStore.edit {
            it[INSTALLED_APP_ID] = installedAppId
            it[AUTH_TOKEN] = authToken
        }
    }

    // ✅ SmartThings 관련 조회
    fun getInstalledAppId(): Flow<String> =
        context.dataStore.data.map { it[INSTALLED_APP_ID] ?: "" }

    fun getSmartThingsAuthToken(): Flow<String> =
        context.dataStore.data.map { it[AUTH_TOKEN] ?: "" }

    // ✅ 키 정의는 companion object에!
    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val INSTALLED_APP_ID = stringPreferencesKey("installed_app_id")
        private val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }
}
