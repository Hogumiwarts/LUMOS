package com.hogumiwarts.lumos.data.entity.remote

data class GetSwitchStatusResponse(
    val tagNumber: Long,
    val deviceId: Long,
    val deviceImg: String?,
    val deviceName: String,
    val deviceManufacturer: String?,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean,
)
