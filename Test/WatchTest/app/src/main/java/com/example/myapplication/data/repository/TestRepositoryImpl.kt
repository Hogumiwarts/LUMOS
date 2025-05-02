package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.api.TestApiService
import com.example.myapplication.data.model.ImuRequest
import com.example.myapplication.data.model.ImuResponse
import com.example.myapplication.domain.repository.TestRepository
import javax.inject.Inject

class TestRepositoryImpl @Inject constructor(
    private val testApiService: TestApiService
):TestRepository {
    override suspend fun postTest(data: ImuRequest): ImuResponse {
        return try {
            testApiService.postTest(data)
        }catch (e: Exception){
            throw e
        }
    }

}