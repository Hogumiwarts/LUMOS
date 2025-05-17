package com.hogumiwarts.domain.model

import com.hogumiwarts.domain.model.audio.AudioVolumeResult
import javax.management.Descriptor

sealed class GestureResult {
    data class Success(
        val data: List<GestureData>
    ) : GestureResult()

    // 구조화된 에러 타입 사용을 위해 수정
    data class Error(val error: CommonError) : GestureResult()
}

data class GestureData(
    val gestureId: Long,
    val gestureName: String,
    val gestureDescription: String,
    val gestureImageUrl: String,
    val routineName: String?,
    val routineId: Long?,
)