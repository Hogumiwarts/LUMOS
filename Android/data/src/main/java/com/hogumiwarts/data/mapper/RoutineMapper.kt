package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.Response.RoutineData
import com.hogumiwarts.domain.model.Routine

fun RoutineData.toDomain(): Routine {
    return Routine(
        routineId = this.routineId,
        routineName = this.routineName,
        routineIcon = this.routineIcon,
        gestureName = this.gestureName
    )
}
