package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.RoutineDetailRequest
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDetailResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface RoutineApi {
    @GET("/routine/api/routine")
    suspend fun getRoutineList(
        @Header("Authorization") accessToken: String
    ): RoutineResponse

    @GET("/routine/api/routine/{routineId}")
    suspend fun getRoutineDetail(
        @Header("Authorization") accessToken: String,
        @Path("routineId") routineId: Int
    ): RoutineDetailResponse
}
