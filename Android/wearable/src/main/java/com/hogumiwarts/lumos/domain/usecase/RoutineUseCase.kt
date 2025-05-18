package com.hogumiwarts.lumos.domain.usecase

import com.hogumiwarts.lumos.domain.model.routine.PostRoutineResult
import com.hogumiwarts.lumos.domain.repository.RoutineRepository
import javax.inject.Inject

class RoutineUseCase @Inject constructor(
    private val routineRepository: RoutineRepository
) {
    suspend fun postRoutineExecute(gestureId: Long): PostRoutineResult {
        val data = routineRepository.executeRoutine(gestureId)
        return data
    }

}