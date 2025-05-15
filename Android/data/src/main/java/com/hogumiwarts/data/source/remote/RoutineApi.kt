package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Response.RoutineResponse
import retrofit2.http.GET
import retrofit2.http.Header

interface RoutineApi {
    @GET("/routine/api/routine")
    suspend fun getRoutineList(
        @Header("Authorization") accessToken: String
    ):RoutineResponse
}
