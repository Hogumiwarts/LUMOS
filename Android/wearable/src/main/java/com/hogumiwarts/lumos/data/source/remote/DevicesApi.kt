package com.hogumiwarts.lumos.data.source.remote

import com.hogumiwarts.lumos.data.entity.remote.BaseResponse
import com.hogumiwarts.lumos.data.entity.remote.GetDevicesResponse
import retrofit2.http.GET

interface DevicesApi {

    @GET("/device/api/devices")
    suspend fun getGestureList(): BaseResponse<List<GetDevicesResponse>>
}