package com.hogumiwarts.domain.model.audio

data class AudioPowerData(
    val tagNumber : Long,
    val deviceId : Long,
    val deviceImg : String,
    val deviceName: String,
    val activated: Boolean
)
