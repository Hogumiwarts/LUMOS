package com.hogumiwarts.data.entity.remote.Response.minibig

data class GetSwitchStatusResponse(
    val tagNumber: Long,
    val deviceId: Int,
    val deviceImg: String?,
    val deviceName: String,
    val manufacturerCode: String?,
    val deviceModel: String,
    val deviceType: String,
    val activated: Boolean,
)
