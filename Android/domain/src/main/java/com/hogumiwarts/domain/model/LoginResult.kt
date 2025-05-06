package com.hogumiwarts.domain.model

sealed class LoginResult {
    data class Success(
        val memberId: Int,
        val email: String,
        val name: String,
        val accessToken: String,
        val refreshToken: String
    ) : LoginResult()

    data class Error(val message: String) : LoginResult()
}