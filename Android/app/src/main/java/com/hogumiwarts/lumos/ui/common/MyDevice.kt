package com.hogumiwarts.lumos.ui.common

import com.hogumiwarts.lumos.ui.screens.routine.components.DeviceListType

data class MyDevice(
    val deviceId: String,
    val deviceName: String,
    val isOn: Boolean,
    val isActive: Boolean, // 활성화 여부
    val deviceType: DeviceListType
) {
    companion object {
        val sample = listOf(
            MyDevice(1.toString(), "거실 공기청정기", true, true, DeviceListType.AIRPURIFIER),
            MyDevice(2.toString(), "침대 조명 스위치", false, true, DeviceListType.SWITCH),
            MyDevice(3.toString(), "내 방 조명", false, true, DeviceListType.LIGHT),
            MyDevice(4.toString(), "음악 플레이어", false, false, DeviceListType.AUDIO),
        )
    }

}

