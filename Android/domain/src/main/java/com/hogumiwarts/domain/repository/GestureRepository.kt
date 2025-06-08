package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.model.gesture.GestureDetailResult

interface GestureRepository {
    suspend fun getGestureList(): GestureResult
    suspend fun getGestureDetail(deviceId: Long): GestureDetailResult
}