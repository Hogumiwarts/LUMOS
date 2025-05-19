package com.hogumiwarts.lumos.data.repostitory


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

            PostRoutineResult.Success(
                data = RoutineMapper.fromPostRoutineResponse(response.data)
            )

        } catch (e: Exception) {
            PostRoutineResult.Error(
                CommonError.NetworkError
            )
        }
    }

}