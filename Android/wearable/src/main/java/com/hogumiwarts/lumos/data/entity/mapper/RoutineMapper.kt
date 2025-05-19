package com.hogumiwarts.lumos.data.entity.mapper

import com.hogumiwarts.lumos.data.entity.remote.routine.PostRoutineResponse
import com.hogumiwarts.lumos.domain.model.routine.PostRoutineData

object RoutineMapper {

    // 🔄 전체 리스트 변환 함수: DTO 리스트 → 도메인 모델 리스트
    fun fromPostRoutineResponse(dtoList: PostRoutineResponse): PostRoutineData {
        return PostRoutineData(success = dtoList.success)
    }

}