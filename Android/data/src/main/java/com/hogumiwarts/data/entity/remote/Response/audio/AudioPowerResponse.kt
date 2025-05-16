package com.hogumiwarts.data.entity.remote.Response.audio

data class AudioPowerResponse(
    val tagNumber : Long,
    val deviceId : Long,
    val deviceImg : String,
    val deviceName: String,
    val activated: Boolean
)
