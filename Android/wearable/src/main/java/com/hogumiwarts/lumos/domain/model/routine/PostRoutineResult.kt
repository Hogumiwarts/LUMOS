package com.hogumiwarts.lumos.domain.model.routine

import com.hogumiwarts.lumos.domain.model.CommonError

sealed class PostRoutineResult {
    data class Success(
        val data: PostRoutineData
    ): PostRoutineResult()

    data class Error(val error: CommonError) : PostRoutineResult()

}