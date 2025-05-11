package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.GestureResult

interface GestureRepository {
    suspend fun getGestureList(): GestureResult
}