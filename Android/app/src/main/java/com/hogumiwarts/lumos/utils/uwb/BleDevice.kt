package com.hogumiwarts.lumos.utils.uwb

data class BleDevice(
    val address: String,
    val name: String?,
    val rssi: Int
)