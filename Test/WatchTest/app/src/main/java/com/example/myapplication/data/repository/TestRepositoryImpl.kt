package com.example.myapplication.data.repository

import android.util.Log
import com.example.myapplication.data.api.TestApiService
import com.example.myapplication.data.model.ImuRequest
import com.example.myapplication.data.model.ImuResponse
import com.example.myapplication.domain.repository.TestRepository
import javax.inject.Inject

private const val TAG = "TestRepositoryImpl"
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

    override suspend fun postPredictTest(data: ImuRequest): ImuResponse {
        return try {
            val a = testApiService.postPredictTest(data)
            Log.d(TAG, "postPredictTest: $a")
            a
        }catch (e: Exception){
            throw e
        }
    }

}