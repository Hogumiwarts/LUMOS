package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.airpurifier.GetAirpurifierResponse
import com.hogumiwarts.data.entity.remote.Response.audio.GetAudioStatusResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface AudioApi {

    @GET("/device/api/devices/{deviceId}/audi/status")
    suspend fun getAudioStatus(@Path("deviceId") deviceId: Long): BaseResponse<GetAudioStatusResponse>
}