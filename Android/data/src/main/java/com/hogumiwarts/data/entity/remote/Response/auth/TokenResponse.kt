package com.hogumiwarts.data.entity.remote.Response.auth

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)