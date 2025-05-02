package com.example.myapplication.domain.usecase

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
}