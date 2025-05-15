package com.hogumiwarts.data.entity.remote.Response

data class RefreshResponse(
    val status: Status,
    val message: String,
    val data: RefreshData
)

data class RefreshData(
    val accessToken: String
)