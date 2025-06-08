package com.hogumiwarts.data.entity.remote.Response.gesture

data class GetGestureDetail(
    val gestureId: Long,
    val gestureName: String,
    val gestureImageUrl: String,
    val gestureDescription: String,

)
