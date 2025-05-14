package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.repository.JwtRepository
import javax.inject.Inject

class SaveTokensUseCase @Inject constructor(
    private val repository: JwtRepository
) {
    suspend operator fun invoke(accessToken: String, refreshToken: String) {
        repository.saveTokens(accessToken, refreshToken)
    }
}