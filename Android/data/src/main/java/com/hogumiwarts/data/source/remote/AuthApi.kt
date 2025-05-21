package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.LoginRequest
import com.hogumiwarts.data.entity.remote.Request.RefreshRequest
import com.hogumiwarts.data.entity.remote.Request.SignupRequest
import com.hogumiwarts.data.entity.remote.Response.auth.LoginResponse
import com.hogumiwarts.data.entity.remote.Response.RefreshResponse
import com.hogumiwarts.data.entity.remote.Response.auth.LogoutResponse
import com.hogumiwarts.data.entity.remote.Response.auth.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("signup")
    suspend fun signup(@Body body: SignupRequest): SignupResponse

    @POST("refresh")
    suspend fun refresh(
        @Body body: RefreshRequest
    ): RefreshResponse

    @POST("logout")
    suspend fun logout(
        @Header("Authorization") accessToken: String
    ): Response<LogoutResponse>
}