package com.hogumiwarts.data.repository

import com.hogumiwarts.data.entity.remote.Response.GestureListData
import com.hogumiwarts.data.entity.remote.Response.GetGestureListResponse
import com.hogumiwarts.data.source.remote.GestureApi
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.repository.GestureRepository
import javax.inject.Inject

class GestureRepositoryImpl@Inject constructor(
    private val gestureApi: GestureApi
) : GestureRepository {

    override suspend fun getGestureList(): GestureResult {
        return try {
            val response: GetGestureListResponse = gestureApi.getGestureList()

            GestureResult.Success(
                data = response.data.map { it.toModel() },
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

fun GestureListData.toModel(): GestureData {
    return GestureData(
        memberGestureId = this.memberGestureId,
        gestureName = this.gestureName,
        description = this.description,
        gestureImg = this.gestureImg,
        routineName = this.routineName
    )
}