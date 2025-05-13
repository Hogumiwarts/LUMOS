package com.hogumiwarts.lumos.domain.model

data class DeviceListData(
    val tagNumber: Long?,
    val deviceId: Long,
    val installedAppId: String,
    val deviceImg: String?,
    val deviceName: String,
    val deviceType: String,
    val activated: Boolean,
)
