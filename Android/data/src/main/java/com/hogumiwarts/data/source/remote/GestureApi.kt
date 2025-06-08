package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.GetGestureListResponse
import com.hogumiwarts.data.entity.remote.Response.audio.GetAudioStatusResponse
import com.hogumiwarts.data.entity.remote.Response.gesture.GetGestureDetail
import retrofit2.http.GET
import retrofit2.http.Path

interface GestureApi {
    @GET("/gesture/api/gesture")
    suspend fun getGestureList(): BaseResponse<List<GetGestureListResponse>>

    @GET("gesture/api/gesture/{gestureId}")
    suspend fun getGestureDetail(@Path("gestureId") gestureId: Long): BaseResponse<GetGestureDetail>
}