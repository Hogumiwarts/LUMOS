package com.hogumiwarts.data.entity.remote.Response

import com.hogumiwarts.data.entity.remote.Request.DeviceRequest
import com.hogumiwarts.domain.model.DeviceResult

data class DeviceResponse(
    val status: Int,
    val message: String,
    val data: List<DeviceResult>
)
