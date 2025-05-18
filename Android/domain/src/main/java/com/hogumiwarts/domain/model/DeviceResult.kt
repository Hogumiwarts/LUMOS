package com.hogumiwarts.domain.model

data class DeviceResult(
    val tagNumber: Int,
    val deviceId: Int,
    val installedAppId: String,
    val deviceImg: String?,       // null 허용
    val deviceName: String,
    val deviceType: String,
    val activated: Boolean
)