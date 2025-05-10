package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.GetGestureListResponse
import com.hogumiwarts.data.entity.remote.LoginRequest
import com.hogumiwarts.data.entity.remote.LoginResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GestureApi {
    @GET("gesture")
    suspend fun getGestureList(): GetGestureListResponse
}