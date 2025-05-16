package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.RoutineResult

interface RoutineRepository {
    suspend fun getRoutineList(accessToken: String): RoutineResult
    suspend fun getRoutineDetail(accessToken: String, routineId: Int): RoutineResult

}