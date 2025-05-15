package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.data.entity.remote.Response.SmartThingsDevice
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType

fun SmartThingsDevice.toMyDevice(isOn: Boolean, isActive: Boolean): MyDevice {
    val category = this.components.firstOrNull()
        ?.categories?.firstOrNull()?.name ?: "Etc"

    val deviceType = DeviceListType.from(category)

    return MyDevice(
        deviceId = deviceId.hashCode().toString(),
        deviceName = label ?: name,
        isOn = isOn,
        isActive = isActive,
        deviceType = deviceType
    )
}