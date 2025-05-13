package com.hogumiwarts.lumos.ui.common

import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType

data class MyDevice(
    val deviceId: Int,
    val deviceName: String,
    val isOn: Boolean,
    val isActive: Boolean, // 활성화 여부
    val deviceType: DeviceListType
) {
    companion object {
        val sample = listOf(
            MyDevice(1, "거실 공기청정기", true, true, DeviceListType.AIR_CLEANER),
            MyDevice(2, "침대 조명 스위치", false, true, DeviceListType.SWITCH),
            MyDevice(3, "내 방 조명", false, true, DeviceListType.LIGHT),
            MyDevice(4, "음악 플레이어", false, false, DeviceListType.SPEAKER),
        )
    }
}