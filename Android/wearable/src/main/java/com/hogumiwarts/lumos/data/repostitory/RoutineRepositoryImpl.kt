package com.hogumiwarts.lumos.data.repostitory


import android.util.Log
import com.hogumiwarts.lumos.data.entity.remote.routine.PostRoutineRequest
import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.data.entity.mapper.RoutineMapper
import com.hogumiwarts.lumos.data.source.remote.RoutineApi
import com.hogumiwarts.lumos.domain.model.routine.PostRoutineResult
import com.hogumiwarts.lumos.domain.repository.RoutineRepository
import javax.inject.Inject


class RoutineRepositoryImpl @Inject constructor(
    private val routineApi: RoutineApi
) : RoutineRepository {

    override suspend fun executeRoutine(gestureId: Long): PostRoutineResult {
        return try {

            val response = routineApi.executeRoutine(gestureId)
            Log.d("결과", "executeRoutine: $response")
            PostRoutineResult.Success(
                data = RoutineMapper.fromPostRoutineResponse(response.data)
            )

        } catch (e: Exception) {
            Log.d("결과", "executeRoutine: $e")
            PostRoutineResult.Error(
                CommonError.NetworkError
            )

        }
    }

}