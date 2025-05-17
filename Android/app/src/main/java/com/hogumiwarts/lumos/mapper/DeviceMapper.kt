package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.model.routine.CommandDevice
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType

fun DeviceResult.toMyDevice(): MyDevice {
    return MyDevice(
        deviceId = deviceId.toString(),
        deviceName = deviceName,
        deviceType = deviceType.toDeviceListType(),
        isOn = activated,
        isActive = activated
    )
}

fun String.toDeviceListType(): DeviceListType {
    return when (this.uppercase()) {
        "SWITCH" -> DeviceListType.SWITCH
        "LIGHT" -> DeviceListType.LIGHT
        "AIRPURIFIER" -> DeviceListType.AIRPURIFIER
        "SPEAKER" -> DeviceListType.AUDIO
        else -> DeviceListType.ETC // enum에 없는 경우 대비
    }
}


fun MyDevice.toCommandDevice(): CommandDevice {
    return CommandDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = deviceType.toString(),
        deviceImageUrl = "",
        commands = emptyList()
    )
}
