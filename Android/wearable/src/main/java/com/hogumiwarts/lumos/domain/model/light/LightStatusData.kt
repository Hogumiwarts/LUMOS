package com.hogumiwarts.lumos.domain.model.light

data class LightStatusData(
    val tagNumber: Long,
    val deviceId: Long,
    val deviceImg: String?,
    val deviceName: String,
    val manufacturerCode: String,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean,
    val lightColor: String,
    val lightTemperature: String,
    val lightCode: String,
)
