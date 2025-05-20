package com.hogumiwarts.lumos.data.source.remote

import com.hogumiwarts.lumos.data.entity.remote.BaseResponse
import com.hogumiwarts.lumos.data.entity.remote.routine.PostRoutineResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface RoutineApi {

    @POST("/routine/api/routine/execute")
    suspend fun executeRoutine(
        @Query("gestureId") gestureId: Long
    ): BaseResponse<PostRoutineResponse>
}