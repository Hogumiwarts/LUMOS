package com.hogumiwarts.myapplication.data.model

data class ImuResponse(
    val status: Int,
    val message: String,
    val data: ImuData,

)
