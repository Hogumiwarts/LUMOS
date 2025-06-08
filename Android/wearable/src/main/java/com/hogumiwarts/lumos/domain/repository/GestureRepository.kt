package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.model.PredictionResult

interface GestureRepository {
    suspend fun predictGesture(normalizedData: Array<FloatArray>): PredictionResult
    suspend fun getGestureList(): GestureResult

}