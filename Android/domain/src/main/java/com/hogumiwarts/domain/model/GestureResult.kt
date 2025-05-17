package com.hogumiwarts.domain.model

import java.io.Serializable
import javax.management.Descriptor

sealed class GestureResult {
    data class Success(
        val data: List<GestureData>
    ) : GestureResult()

    // 구조화된 에러 타입 사용을 위해 수정
    object InvalidPassword : GestureResult()
    object UserNotFound : GestureResult()
    object UnknownError : GestureResult()
    object NetworkError : GestureResult()
}

data class GestureData(
    val memberGestureId: Long,
    val gestureName: String,
    val description: String,
    val gestureImg: String,
    val routineName: String
) : Serializable {
    companion object {
        val EMPTY = GestureData(
            memberGestureId = -1,
            gestureName = "",
            description = "",
            gestureImg = "",
            routineName = ""
        )
    }
}
