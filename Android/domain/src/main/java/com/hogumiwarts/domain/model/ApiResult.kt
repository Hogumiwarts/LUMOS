package com.hogumiwarts.domain.model

sealed class ApiResult<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : ApiResult<T>(data)
    class Error<T>(message: String, data: T? = null) : ApiResult<T>(data, message)
    class Loading<T>(data: T? = null) : ApiResult<T>(data)
    class Fail<T>(message: String = "네트워크 연결 오류", data: T? = null) : ApiResult<T>(data, message)

    companion object {
        fun <T> success(data: T): ApiResult<T> = Success(data)
        fun <T> error(message: String, data: T? = null): ApiResult<T> = Error(message, data)
        fun <T> loading(data: T? = null): ApiResult<T> = Loading(data)
        fun <T> fail(message: String = "네트워크 연결 오류", data: T? = null): ApiResult<T> = Fail(message, data)
    }
}