package com.hogumiwarts.data.entity.remote.Response

data class GetGestureListResponse(
    val status: Int,
    val message: String,
    val data: List<GestureListData>
)

data class GestureListData(
    val memberGestureId: Long,
    val gestureName: String,
    val gestureImg: String,
    val description: String,
    val routineName: String
)
