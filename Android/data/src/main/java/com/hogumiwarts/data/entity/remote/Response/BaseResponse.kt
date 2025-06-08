package com.hogumiwarts.data.entity.remote.Response

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null
)
