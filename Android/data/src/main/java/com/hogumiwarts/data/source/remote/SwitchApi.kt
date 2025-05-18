package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.PatchControlResponse
import com.hogumiwarts.data.entity.remote.Response.minibig.GetSwitchStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface SwitchApi {
    @GET("/device/api/devices/{deviceId}/switch/status")
    suspend fun getSwitchStatus(@Path("deviceId") deviceId: Int): BaseResponse<GetSwitchStatusResponse>

    @PATCH("/device/api/devices/{deviceId}/switch/power")
    suspend fun getSwitchPower(@Path("deviceId") deviceId: Int, @Body request: PowerRequest): BaseResponse<PatchControlResponse>
}