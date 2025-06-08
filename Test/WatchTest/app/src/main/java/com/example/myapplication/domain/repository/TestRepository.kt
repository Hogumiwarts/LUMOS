package com.example.myapplication.domain.repository

import com.example.myapplication.data.model.ImuRequest
import com.example.myapplication.data.model.ImuResponse

interface TestRepository {
    suspend fun postTest(data: ImuRequest) : ImuResponse

    suspend fun postPredictTest(data: ImuRequest) : ImuResponse
}