package com.hogumiwarts.data.entity.remote.Response.routine

data class RoutineDetailResponse(
    val status: Int,
    val message: String,
    val data: RoutineDetailData
)

data class RoutineDetailData(
    val routineName: String,
    val routineIcon: String,
    val devices: RoutineDeviceData,
    val gestureId: Int,
    val gestureName: String,
    val gestureImageUrl: String,
    val gestureDescription: String
)

data class RoutineDeviceData (
    val deviceId: Int,
    val deviceName: String,
    val deviceType: String,
    val deviceImageUrl: String,
    val commands: SwitchSettingData
)

data class SwitchSettingData (
    val component: String,
    val capability: String,
    val command: String,

)
