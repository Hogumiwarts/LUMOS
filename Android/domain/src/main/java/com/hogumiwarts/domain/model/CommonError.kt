package com.hogumiwarts.domain.model

sealed class CommonError {
    object UserNotFound : CommonError()
    object UnauthorizedAccess : CommonError()
    object UnknownError : CommonError()
    object NetworkError : CommonError()
}