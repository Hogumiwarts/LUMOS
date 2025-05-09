package com.example.myapplication.data.api

import com.example.myapplication.data.model.ImuRequest
import com.example.myapplication.data.model.ImuResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TestApiService {
    @POST("/gesture-sensor/api/sensor")
    suspend fun postTest(@Body data: ImuRequest): ImuResponse
}