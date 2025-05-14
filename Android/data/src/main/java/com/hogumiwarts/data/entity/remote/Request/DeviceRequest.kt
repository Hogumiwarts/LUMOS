package com.hogumiwarts.data.entity.remote.Request

data class DeviceRequest(
    val tagNumber: Int,
    val deviceId: Int,
    val installedAppId: String,
    val deviceImg: String?,
    val deviceName: String,
    val deviceType: String,
    val activated: Boolean
)
