package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.LoginResult

// 로그인 기능을 위한 인터페이스
interface AuthRepository {
    suspend fun login(email: String, password: String): LoginResult
}