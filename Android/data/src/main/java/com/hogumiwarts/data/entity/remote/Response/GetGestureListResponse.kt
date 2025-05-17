package com.hogumiwarts.data.entity.remote.Response

data class GetGestureListResponse(
    val memberGestureId: Long,
    val gestureName: String,
    val gestureDescription: String,
    val gestureImageUrl: String,
    val routineName: String,
    val routineId: Long,
)
