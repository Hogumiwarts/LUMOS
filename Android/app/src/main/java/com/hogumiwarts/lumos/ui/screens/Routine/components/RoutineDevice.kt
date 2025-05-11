package com.hogumiwarts.lumos.ui.screens.Routine.components

import com.hogumiwarts.lumos.R
import com.hogumiwarts.lumos.ui.common.MyDevice

data class RoutineDevice(
    val deviceId: Int,
    val deviceName: String,
    val deviceType: DeviceListType,
    val isOn: Boolean
) {
    val iconResId get() = deviceType.iconResId
    val color get() = deviceType.color
    val deviceTypeName get() = deviceType.deviceName


    companion object {
        val sample = listOf(
            RoutineDevice(1, "거실 공기청정기", DeviceListType.AIR_CLEANER, true),
            RoutineDevice(2, "내 방 조명 1", DeviceListType.LIGHT, false)
        )
    }

}

fun MyDevice.toRoutineDevice(): RoutineDevice {
    return RoutineDevice(
        deviceId = deviceId,
        deviceName = deviceName,
        deviceType = deviceType,
        isOn = isOn
    )
}
