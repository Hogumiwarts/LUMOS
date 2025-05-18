package com.hogumiwarts.lumos.domain.repository

import com.hogumiwarts.lumos.domain.model.routine.PostRoutineResult

interface RoutineRepository {

    suspend fun executeRoutine(gestureId: Long): PostRoutineResult
}