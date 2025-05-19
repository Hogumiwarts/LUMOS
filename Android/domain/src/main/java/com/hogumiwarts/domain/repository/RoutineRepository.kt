package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.routine.CreateRoutineParam
import com.hogumiwarts.domain.model.routine.RoutineResult

interface RoutineRepository {
    suspend fun getRoutineList(accessToken: String): RoutineResult
    suspend fun getRoutineDetail(accessToken: String, routineId: Long): RoutineResult
    suspend fun createRoutine(
        result: CreateRoutineParam,
        accessToken: String
    ): RoutineResult

    suspend fun updateRoutine(
        routineId: Long,
        result: CreateRoutineParam,
        accessToken: String
    ): RoutineResult

    suspend fun deleteRoutine(
        accessToken: String,
        routineId: Long
    ): RoutineResult
}