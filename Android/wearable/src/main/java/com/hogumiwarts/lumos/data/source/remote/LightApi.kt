package com.hogumiwarts.lumos.data.source.remote

import com.hogumiwarts.lumos.data.entity.remote.BaseResponse
import com.hogumiwarts.lumos.data.entity.remote.GetSwitchStatusResponse
import com.hogumiwarts.lumos.data.entity.remote.light.GetLightStatusResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface LightApi {

    @GET("/device/api/devices/{deviceId}/light/status")
    suspend fun getLightStatus(@Path("deviceId") deviceId: Long): BaseResponse<GetLightStatusResponse>

}