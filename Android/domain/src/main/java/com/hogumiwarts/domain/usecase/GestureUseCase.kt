package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.model.audio.AudioStatusResult
import com.hogumiwarts.domain.model.gesture.GestureDetailResult
import com.hogumiwarts.domain.repository.AudioRepository
import com.hogumiwarts.domain.repository.GestureRepository
import javax.inject.Inject

class GestureUseCase@Inject constructor(
    private val gestureRepository: GestureRepository
) {
    suspend fun getGesture(): GestureResult {

        val data = gestureRepository.getGestureList()
        return data
    }

    suspend fun getGestureDetail(deviceId: Long): GestureDetailResult {

        val data = gestureRepository.getGestureDetail(deviceId)
        return data
    }


}