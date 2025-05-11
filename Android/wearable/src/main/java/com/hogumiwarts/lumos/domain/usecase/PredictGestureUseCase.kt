package com.hogumiwarts.lumos.domain.usecase

import com.hogumiwarts.lumos.domain.model.GestureResult
import com.hogumiwarts.lumos.domain.repository.GestureRepository

class PredictGestureUseCase(
    private val repository: GestureRepository
) {
    suspend operator fun invoke(data: Array<FloatArray>): GestureResult {
        return repository.predictGesture(data)
    }
}