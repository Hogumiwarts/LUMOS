package com.hogumiwarts.data.mapper

import com.hogumiwarts.domain.model.DeviceResult

fun DeviceResult.toDomain(): DeviceResult {
    return DeviceResult(
        tagNumber = tagNumber,
        deviceId = deviceId,
        installedAppId = installedAppId,
        deviceImg = deviceImg,
        deviceName = deviceName,
        deviceType = deviceType,
        activated = activated
    )
}