package com.hogumiwarts.data.entity.remote.Response

data class SignupResponse(
    val status: Int,
    val message: String,
    val data: SignupData
)

data class SignupData(
    val memberId: Long,
    val email: String,
    val name: String,
    val createdAt: String?
)