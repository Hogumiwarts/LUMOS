package com.hogumiwarts.lumos.data

data class GestureData(
    val timestamp: Long,
    val liAccX: Float,
    val liAccY: Float,
    val liAccZ: Float,
    val accX: Float,
    val accY: Float,
    val accZ: Float,
    val gryoX: Float,
    val gryoY: Float,
    val gryoZ: Float,
)
