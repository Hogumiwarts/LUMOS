package com.hogumiwarts.data.entity.remote.Response.light

data class GetLightStatusResponse(
    val tagNumber: Long,
    val deviceId: Long,
    val deviceImg: String?,
    val deviceName: String,
    val manufacturerCode: String,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean,
    val brightness: Int,
    val lightTemperature: Int,
    val hue: Int,
    val saturation: Float,
)