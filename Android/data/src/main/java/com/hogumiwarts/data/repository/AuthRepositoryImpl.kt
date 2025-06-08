package com.hogumiwarts.data.repository

import android.util.Log
import com.hogumiwarts.data.entity.remote.Request.LoginRequest
import com.hogumiwarts.data.entity.remote.Request.SignupRequest
import com.hogumiwarts.data.entity.remote.Response.auth.LoginResponse
import com.hogumiwarts.data.entity.remote.Response.auth.SignupResponse
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.domain.model.LoginResult
import com.hogumiwarts.domain.model.SignupResult
import com.hogumiwarts.domain.repository.AuthRepository
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi
) : AuthRepository {

    // 로그인
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

    // 회원 가입
    override suspend fun signup(
        email: String,
        password1: String,
        password2: String,
        name: String
    ): SignupResult {
        return try {
            val request = SignupRequest(email, password1, password2, name)
            Timber.tag("SignupRequest").d("보내는 요청: " + request)

            val response: SignupResponse =
                authApi.signup(request)

            SignupResult.Success(
                memberId = response.data.memberId,
                email = response.data.email,
                name = response.data.name,
                createdAt = response.data.createdAt
            )
        } catch (e: HttpException) {
            when (e.code()) {
                400 -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    Log.e("Signup", "서버 응답 상태: ${e.code()} / 메시지: $errorBody")

                    when {
                        errorBody?.contains("SIGNUP-001") == true -> SignupResult.EmailAlreadyExists
                        errorBody?.contains("SIGNUP-002") == true -> SignupResult.PasswordMismatch
                        else -> SignupResult.UnknownError
                    }
                }

                else -> {
                    SignupResult.NetworkError
                }
            }
        } catch (e: IOException) {
            SignupResult.NetworkError
        } catch (e: Exception) {
            Log.e("SignupRepository", "Unexpected error", e)
            SignupResult.UnknownError
        }
    }

    // 로그아웃
    override suspend fun logout(accessToken: String): Boolean {
        return try {
            val response = authApi.logout("Bearer $accessToken")
            response.isSuccessful && response.body()?.data?.success == true
        } catch (e: Exception) {
            Timber.e("Logout failed: ${e.message}")
            false
        }
    }
}