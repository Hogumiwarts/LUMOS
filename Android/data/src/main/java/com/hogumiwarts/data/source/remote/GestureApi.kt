package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.GetGestureListResponse
import retrofit2.http.GET

interface GestureApi {
    @GET("/gesture/api/routine/gesture")
    suspend fun getGestureList(): GetGestureListResponse
}