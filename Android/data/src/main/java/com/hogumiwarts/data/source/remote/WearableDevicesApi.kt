package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.device.GetDevicesResponse
import com.hogumiwarts.domain.model.DeviceResult
import retrofit2.http.GET

interface WearableDevicesApi {
    @GET("/device/api/devices")
    suspend fun getGestureList(): BaseResponse<List<GetDevicesResponse>>
}