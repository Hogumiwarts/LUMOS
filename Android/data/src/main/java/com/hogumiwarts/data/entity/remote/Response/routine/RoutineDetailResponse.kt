package com.hogumiwarts.data.entity.remote.Response.routine

import com.hogumiwarts.domain.model.CommandData
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class RoutineDetailResponse(
    val status: Int,
    val message: String,
    val data: RoutineDetailData
)

data class RoutineDetailData(
    val routineName: String,
    val routineIcon: String,
    val devices: List<RoutineDeviceData>,
    val gestureId: Int,
    val gestureName: String,
    val gestureImageUrl: String,
    val gestureDescription: String
)

data class RoutineDeviceData(
    val deviceId: Int,
    val deviceName: String,
    val deviceType: String,
    val deviceImageUrl: String?,
    val commands: List<com.hogumiwarts.domain.model.CommandData>
)
