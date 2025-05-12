package com.hogumiwarts.data.entity.remote.Request

data class SignupRequest (
    val email: String,
    val password1: String,
    val password2: String,
    val name: String
)