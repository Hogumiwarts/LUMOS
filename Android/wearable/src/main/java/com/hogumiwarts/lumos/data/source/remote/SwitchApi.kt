package com.hogumiwarts.lumos.data.source.remote

import com.hogumiwarts.lumos.data.entity.remote.BaseResponse
import com.hogumiwarts.lumos.data.entity.remote.GetDevicesResponse
import com.hogumiwarts.lumos.data.entity.remote.GetSwitchStatusResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SwitchApi {
    @GET("/device/api/devices/{deviceId}/switch/status")
    suspend fun getSwitchStatus(@Path("deviceId") deviceId: Long): BaseResponse<GetSwitchStatusResponse>
}