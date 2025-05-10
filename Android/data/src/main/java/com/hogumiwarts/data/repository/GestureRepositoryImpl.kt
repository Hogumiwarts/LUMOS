package com.hogumiwarts.data.repository

import com.hogumiwarts.data.entity.remote.GetGestureListResponse
import com.hogumiwarts.data.entity.remote.LoginRequest
import com.hogumiwarts.data.entity.remote.LoginResponse
import com.hogumiwarts.data.source.remote.AuthApi
import com.hogumiwarts.data.source.remote.GestureApi
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.model.LoginResult
import com.hogumiwarts.domain.repository.AuthRepository
import com.hogumiwarts.domain.repository.GestureRepository
import javax.inject.Inject

class GestureRepositoryImpl@Inject constructor(
    private val gestureApi: GestureApi
) : GestureRepository {

    override suspend fun getGestureList(): GestureResult {
        return try {
            val response: GetGestureListResponse = gestureApi.getGestureList()

            GestureResult.Success(
                memberGestureId = response.data.memberGestureId,
                gestureName = response.data.gestureName,
                description = response.data.description,
                gestureImg = response.data.gestureImg,
                routineName = response.data.routineName,
            )
        } catch (e: retrofit2.HttpException) {
            // 구조화된 에러 타입 사용을 위해 수정
            when (e.code()) {
                400 -> GestureResult.InvalidPassword
                404 -> GestureResult.UserNotFound
                else -> GestureResult.UnknownError
            }
        } catch (e: Exception) {
            GestureResult.NetworkError
        }
    }
}