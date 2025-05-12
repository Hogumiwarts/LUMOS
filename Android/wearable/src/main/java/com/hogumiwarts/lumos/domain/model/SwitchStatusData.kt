package com.hogumiwarts.lumos.domain.model

data class SwitchStatusData(
    val tagNumber: Long,
    val deviceId: Long,
    val deviceImg: String?,
    val deviceName: String,
    val deviceManufacturer: String?,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean,
)
