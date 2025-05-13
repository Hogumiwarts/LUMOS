package com.hogumiwarts.lumos.data.source.remote

import com.hogumiwarts.lumos.data.entity.remote.BaseResponse
import com.hogumiwarts.lumos.data.entity.remote.PatchSwitchPowerResponse
import com.hogumiwarts.lumos.data.entity.remote.GetSwitchStatusResponse
import com.hogumiwarts.lumos.data.entity.remote.SwitchPowerRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface SwitchApi {
    @GET("/device/api/devices/{deviceId}/switch/status")
    suspend fun getSwitchStatus(@Path("deviceId") deviceId: Long): BaseResponse<GetSwitchStatusResponse>

    @PATCH("/device/api/devices/{deviceId}/switch/power")
    suspend fun getSwitchPower(@Path("deviceId") deviceId: Long, @Body request: SwitchPowerRequest): BaseResponse<PatchSwitchPowerResponse>
}