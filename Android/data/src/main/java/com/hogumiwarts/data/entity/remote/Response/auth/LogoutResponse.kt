package com.hogumiwarts.data.entity.remote.Response.auth

data class LogoutResponse(
    val status: Int,
    val message: String,
    val data: LogoutData
)

data class LogoutData(
    val success: Boolean
)