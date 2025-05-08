package com.hogumiwarts.lumos.data

data class LightData(
    val deviceId: Long,
    val tagNumber: Long,
    val deviceName: String,
    val deviceImg: String,
    val lightColor: String,
    val activated : Boolean
)
