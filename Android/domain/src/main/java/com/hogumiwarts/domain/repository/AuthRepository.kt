package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.LoginResult
import com.hogumiwarts.domain.model.SignupResult

// auth 기능을 위한 인터페이스
interface AuthRepository {
    suspend fun login(email: String, password: String): LoginResult

    suspend fun signup(
        email: String,
        password1: String,
        password2: String,
        name: String
    ): SignupResult
}