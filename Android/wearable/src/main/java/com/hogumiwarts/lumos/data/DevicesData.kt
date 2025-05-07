package com.hogumiwarts.lumos.data

data class DevicesData(
    val tagNumber : Long,
    val deviceId : Long,
    val installedAppId : String,
    val deviceImg: String,
    val deviceName: String,
    val activated: Boolean
)
