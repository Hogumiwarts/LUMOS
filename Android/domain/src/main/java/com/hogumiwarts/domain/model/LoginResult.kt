package com.hogumiwarts.domain.model

import javax.annotation.processing.Messager


sealed class LoginResult {
    data class Success(
        val memberId: Int,
        val email: String,
        val name: String,
        val accessToken: String,
        val refreshToken: String
    ) : LoginResult()

//    data class Error(val message: String) : LoginResult()

    // 구조화된 에러 타입 사용을 위해 수정
    object InvalidPassword : LoginResult()
    object UserNotFound : LoginResult()
    object UnknownError : LoginResult()
    object NetworkError : LoginResult()
}