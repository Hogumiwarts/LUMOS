package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.lumos.domain.model.GestureResult

interface GestureRepository {
    suspend fun predictGesture(normalizedData: Array<FloatArray>): GestureResult
}