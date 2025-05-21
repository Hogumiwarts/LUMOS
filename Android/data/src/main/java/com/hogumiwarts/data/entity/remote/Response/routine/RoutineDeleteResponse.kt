package com.hogumiwarts.data.entity.remote.Response.routine

data class RoutineDeleteResponse(
    val status: Int,
    val message: String,
    val data: DeleteData
)

data class DeleteData(
    val success: Boolean
)