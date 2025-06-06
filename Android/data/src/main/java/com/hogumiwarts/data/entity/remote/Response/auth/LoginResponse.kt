package com.hogumiwarts.data.entity.remote.Response.auth

data class LoginResponse(
    val status: Int,
    val message: String,
    val data: LoginData
)

data class LoginData(
    val memberId: Int,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String
)