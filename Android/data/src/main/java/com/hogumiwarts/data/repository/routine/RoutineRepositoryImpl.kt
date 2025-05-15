package com.hogumiwarts.data.repository.routine

import com.hogumiwarts.data.source.remote.RoutineApi
import com.hogumiwarts.domain.model.RoutineResult
import com.hogumiwarts.domain.repository.RoutineRepository
import javax.inject.Inject
import com.hogumiwarts.data.mapper.toDomain


class RoutineRepositoryImpl @Inject constructor(
    private val routineApi: RoutineApi
) : RoutineRepository {
    override suspend fun getRoutineList(accessToken: String): RoutineResult {
        return try {
            val response = routineApi.getRoutineList("Bearer $accessToken")
            val routines = response.data.toDomain()

            RoutineResult.Success(routines)

        } catch (e: Exception) {
            RoutineResult.Failure(e.message ?: "⚠️ 루틴 리스트 불러오기 실패")
        }
    }
}