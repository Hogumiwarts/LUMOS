package com.hogumiwarts.lumos.data.source.remote

import com.hogumiwarts.data.entity.remote.Request.routine.PostRoutineRequest
import com.hogumiwarts.lumos.data.entity.remote.routine.PostRoutineResponse
import com.hogumiwarts.lumos.data.entity.remote.BaseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RoutineApi {

    @POST("/routine/api/routine/execute")
    suspend fun executeRoutine(
        @Body request: PostRoutineRequest
    ): BaseResponse<PostRoutineResponse>
}