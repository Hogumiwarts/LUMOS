package com.hogumiwarts.data.entity.remote.Response

data class MemberResponse(
    val status: Int,
    val message: String,
    val data: MemberData
)

data class MemberData(
    val name: String
)
