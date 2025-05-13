package com.hogumiwarts.data.entity.remote.Response

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)