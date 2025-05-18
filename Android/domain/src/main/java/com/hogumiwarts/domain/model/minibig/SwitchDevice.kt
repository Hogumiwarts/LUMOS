package com.hogumiwarts.domain.model.minibig

data class SwitchDevice(
    val tagNumber: Int,
    val deviceId: Int,
    val deviceImg: String,
    val deviceName: String,
    val manufactureCode: String,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean
)