package com.hogumiwarts.domain.model

sealed class MemberResult {
    data class Success(val name: String) : MemberResult()
    data class Failure(val message: String) : MemberResult()
}
