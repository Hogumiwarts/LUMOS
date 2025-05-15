package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.RoutineData
import com.hogumiwarts.domain.model.Routine

fun List<RoutineData>.toDomain(): List<Routine> {
    return this.map {
        Routine(
            routineId = it.routineId,
            routineName = it.routineName,
            routineIcon = it.routineIcon,
            gestureName = it.gestureName
        )
    }
}
