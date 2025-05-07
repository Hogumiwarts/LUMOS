package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.LoginRequest
import com.hogumiwarts.data.entity.remote.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}