package com.hogumiwarts.data.repository.airpurifier

import android.util.Log
import com.hogumiwarts.data.mapper.AirpurifierMapper
import com.hogumiwarts.data.source.remote.AirpurifierApi
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.repository.AirpurifierRepository
import javax.inject.Inject

class AirpurifierRepositoryImpl@Inject constructor(
    private val airpurifierApi: AirpurifierApi // 🔹 Retrofit API 인터페이스 주입
):AirpurifierRepository {
    override suspend fun getAirpurifierStatus(deviceId: Long): AirpurifierResult {
        return try {
            // ✅ API 호출
            val response = airpurifierApi.getAirpurifierStatus(deviceId)

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            AirpurifierResult.Success(
                data = AirpurifierMapper.fromAirpurifierDataResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> AirpurifierResult.Error(CommonError.UserNotFound)
                else -> AirpurifierResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            AirpurifierResult.Error(CommonError.NetworkError)
        }
    }
}