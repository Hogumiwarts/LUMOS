package com.hogumiwarts.lumos.ui.screens.routine.components

import com.hogumiwarts.lumos.ui.common.MyDevice

data class RoutineDevice(
    val deviceId: String,
    val deviceName: String,
    val deviceType: DeviceListType,
    val isOn: Boolean
) {
    val iconResId get() = deviceType.iconResId
    val color get() = deviceType.color
    val deviceTypeName get() = deviceType.categoryName


    companion object {
        val sample = listOf(
            RoutineDevice("1", "거실 공기청정기", DeviceListType.AIRPURIFIER, true),
            RoutineDevice("3", "내 방 조명", DeviceListType.LIGHT, false)
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
