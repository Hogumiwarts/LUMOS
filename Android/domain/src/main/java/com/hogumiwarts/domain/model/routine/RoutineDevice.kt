package com.hogumiwarts.domain.model.routine


data class RoutineDevice(
    val deviceId: String,
    val deviceName: String,
    val deviceType: String,
    val isOn: Boolean
)