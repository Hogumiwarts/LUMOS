package com.hogumiwarts.lumos.data.entity.remote

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T
)
