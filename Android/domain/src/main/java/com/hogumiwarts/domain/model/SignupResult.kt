package com.hogumiwarts.domain.model

sealed class SignupResult {
    data class Success(
        val memberId: Long,
        val email: String,
        val name: String,
        val createdAt: String?
    ) : SignupResult()

    object EmailAlreadyExists : SignupResult()
    object PasswordMismatch : SignupResult()
    object NetworkError : SignupResult()
    object UnknownError : SignupResult()
}