package com.hogumiwarts.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.routine.RoutineCreateRequest
import com.hogumiwarts.data.entity.remote.Response.BaseResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineCreateResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDeleteResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineDetailResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineEditResponse
import com.hogumiwarts.data.entity.remote.Response.routine.RoutineResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoutineApi {
    @GET("/routine/api/routine")
    suspend fun getRoutineList(
        @Header("Authorization") accessToken: String
    ): RoutineResponse

    @GET("/routine/api/routine/{routineId}")
    suspend fun getRoutineDetail(
        @Header("Authorization") accessToken: String,
        @Path("routineId") routineId: Long
    ): RoutineDetailResponse

    @POST("/routine/api/routine")
    suspend fun makeRoutine(
        @Header("Authorization") accessToken: String,
        @Body body: RoutineCreateRequest
    ): RoutineCreateResponse

    @PUT("/routine/api/routine/{routineId}")
    suspend fun updateRoutine(
        @Path("routineId") routineId: Long,
        @Body request: RoutineCreateRequest
    ): BaseResponse<RoutineEditResponse>

    @DELETE("/routine/api/routine/{routineId}")
    suspend fun deleteRoutine(
        @Header("Authorization") accessToken: String,
        @Path("routineId") routineId: Long
    ): RoutineDeleteResponse
}
