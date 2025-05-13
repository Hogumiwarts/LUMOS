package com.hogumiwarts.lumos.domain.model

sealed class CommonError {
    object UserNotFound : CommonError()
    object UnknownError : CommonError()
    object NetworkError : CommonError()
}