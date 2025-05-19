package com.hogumiwarts.data.entity.remote.Response.device


data class GetDevicesResponse(
    val tagNumber: Long?,
    val deviceId: Long,
    val installedAppId: String,
    val deviceImg: String?,
    val deviceName: String,
    val deviceType: String,
    val activated: Boolean,
)