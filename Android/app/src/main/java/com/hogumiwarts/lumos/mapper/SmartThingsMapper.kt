package com.hogumiwarts.lumos.mapper

import com.hogumiwarts.data.entity.remote.Response.SmartThingsDevice
import com.hogumiwarts.lumos.ui.common.MyDevice
import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType

fun SmartThingsDevice.toMyDevice(): MyDevice {
    val category = this.components.firstOrNull()
        ?.categories?.firstOrNull()?.name ?: "Etc"

    val deviceType = DeviceListType.from(category)


    val hasSwitch = this.components
        .flatMap { it.capabilities }
        .any { it.id == "switch" }

    return MyDevice(
        deviceId = deviceId.hashCode(),
        deviceName = label ?: name,
        isOn = hasSwitch,
        isActive = true,
        deviceType = deviceType
    )
}