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
            when (e.code()) {
                400 -> LoginResult.Error("잘못된 비밀번호입니다.")
                404 -> LoginResult.Error("해당 이메일을 가진 사용자가 없습니다.")
                else -> LoginResult.Error("알 수 없는 오류가 발생했습니다.")
            }
        } catch (e: Exception) {
            LoginResult.Error("네트워크 오류가 발생했습니다.")
        }
    }
}