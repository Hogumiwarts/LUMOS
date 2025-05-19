package com.hogumiwarts.domain.model.gesture

import com.hogumiwarts.domain.model.CommonError

sealed class GestureDetailResult {
    data class Success(
        val data : GestureDetailData
    ): GestureDetailResult()

    data class Error(val error: CommonError) : GestureDetailResult()
}