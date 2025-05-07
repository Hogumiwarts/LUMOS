package com.hogumiwarts.data.repository

import com.hogumiwarts.data.entity.remote.LoginRequest
import com.hogumiwarts.data.entity.remote.LoginResponse
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.domain.model.LoginResult
import com.hogumiwarts.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {
    override suspend fun login(email: String, password: String): LoginResult {
        return try {
            val response: LoginResponse = authApi.login(LoginRequest(email, password))

            LoginResult.Success(
                memberId = response.data.memberId,
                email = response.data.email,
                name = response.data.name,
                accessToken = response.data.accessToken,
                refreshToken = response.data.refreshToken
            )
        } catch (e: retrofit2.HttpException) {
            // 구조화된 에러 타입 사용을 위해 수정
            when (e.code()) {
                400 -> LoginResult.InvalidPassword
                404 -> LoginResult.UserNotFound
                else -> LoginResult.UnknownError
            }
        } catch (e: Exception) {
            LoginResult.NetworkError
        }
    }
}