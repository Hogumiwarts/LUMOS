package com.hogumiwarts.lumos.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object TokenDataStore {
    // Context 확장 프로퍼티로 DataStore 정의
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token_prefs")

    // 키 정의
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

    // 저장 함수
    suspend fun saveTokens(context: Context, accessToken: String, refreshToken: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    // AccessToken 가져오기
    fun getAccessToken(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[ACCESS_TOKEN_KEY] ?: ""
        }
    }

    // RefreshToken 가져오기
    fun getRefreshToken(context: Context): Flow<String> {
        return context.dataStore.data.map { prefs ->
            prefs[REFRESH_TOKEN_KEY] ?: ""
        }
    }

    // 삭제 함수 (선택)
    suspend fun clearTokens(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}