package com.hogumiwarts.data.repository

import android.util.Log
import com.hogumiwarts.data.entity.remote.Response.GetGestureListResponse
import com.hogumiwarts.data.mapper.AudioMapper
import com.hogumiwarts.data.source.remote.GestureApi
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.GestureData
import com.hogumiwarts.domain.model.GestureResult
import com.hogumiwarts.domain.model.audio.AudioPowerResult
import com.hogumiwarts.domain.repository.GestureRepository
import javax.inject.Inject
import kotlin.math.log

class GestureRepositoryImpl@Inject constructor(
    private val gestureApi: GestureApi
) : GestureRepository {

    override suspend fun getGestureList(): GestureResult {
        return try {
            val response = gestureApi.getGestureList()
            Log.d("Post", "getGestureList: $response")
            GestureResult.Success(
                data = response.data.map { it.toModel() },
            )
        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> GestureResult.Error(CommonError.UserNotFound)
                else -> GestureResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            Log.d("Post", "getGestureList: error $e")
            GestureResult.Error(CommonError.NetworkError)
        }
    }
}

fun GetGestureListResponse.toModel(): GestureData {
    return GestureData(
        gestureId = this.gestureId,
        gestureName = this.gestureName,
        gestureDescription = this.gestureDescription,
        gestureImageUrl = this.gestureImageUrl,
        routineName = this.routineName?: "",
        routineId = this.routineId ?: 0L

    )
}