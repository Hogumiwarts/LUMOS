package com.hogumiwarts.domain.model.minibig

data class SwitchStatusData(
    val tagNumber: Long?,
    val deviceId: Int,
    val deviceImg: String?,
    val deviceName: String,
    val manufacturerCode: String?,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean,
)
