package com.example.myapplication.domain.usecase

import android.util.Log
import com.example.myapplication.data.model.ImuRequest
import com.example.myapplication.data.model.ImuResponse
import com.example.myapplication.domain.repository.TestRepository
import javax.inject.Inject

class TestUseCase@Inject constructor(
    private val testRepository: TestRepository
) {
    suspend fun postTest(data: ImuRequest): Result<ImuResponse> {
        return try {
            val response = testRepository.postTest(data)
            Result.success(response)  // ✅ 성공 시 Result.success 반환
        } catch (e: Exception) {
            Result.failure(e)  // ✅ 실패 시 Result.failure 반환
        }
    }

    suspend fun postPredictTest(data: ImuRequest): Result<ImuResponse> {
        return try {
            val response = testRepository.postPredictTest(data)
            Log.d("TAG", "postTest: $response")
            Result.success(response)  // ✅ 성공 시 Result.success 반환
        } catch (e: Exception) {
            Result.failure(e)  // ✅ 실패 시 Result.failure 반환
        }
    }
}