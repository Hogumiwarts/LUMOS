package com.hogumiwarts.data.entity.remote.Response

data class GetGestureListResponse(
    val gestureId: Long,
    val gestureName: String,
    val gestureDescription: String,
    val gestureImageUrl: String,
    val routineName: String?,
    val routineId: Long?,
)
