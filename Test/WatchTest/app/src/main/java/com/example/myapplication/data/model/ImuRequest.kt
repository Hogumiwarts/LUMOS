package com.example.myapplication.data.model

data class ImuRequest(
    val gestureId: Long,
    val watchDeviceId: String,
    val data: List<GestureData>
)
