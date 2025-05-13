package com.hogumiwarts.lumos.data.repostitory

import android.util.Log
import com.hogumiwarts.lumos.data.entity.mapper.DeviceMapper
import com.hogumiwarts.lumos.data.entity.mapper.LightMapper
import com.hogumiwarts.lumos.data.entity.mapper.SwitchMapper
import com.hogumiwarts.lumos.data.entity.remote.PowerRequest
import com.hogumiwarts.lumos.data.source.remote.LightApi
import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.lumos.domain.model.light.GetLightStatusResult
import com.hogumiwarts.lumos.domain.repository.LightRepository
import javax.inject.Inject

class LightRepositoryImpl@Inject constructor(
    private val lightApi: LightApi // 🔹 Retrofit API 인터페이스 주입
) : LightRepository {

    // 🔸 기기 데이터를 API로부터 받아오는 함수
    override suspend fun getLightStatus(deviceId: Long): GetLightStatusResult {
        return try {
            // ✅ API 호출
            val response = lightApi.getLightStatus(deviceId)

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            GetLightStatusResult.Success(
                data = LightMapper.fromSwitchStatusDataResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> GetLightStatusResult.Error(CommonError.UserNotFound)
                else -> GetLightStatusResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            GetLightStatusResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchLightPower(deviceId: Long, activated: Boolean): PatchSwitchPowerResult {
        return try {
            // ✅ API 호출
            val response = lightApi.getSwitchPower(deviceId, PowerRequest(activated))

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            PatchSwitchPowerResult.Success(
                data = DeviceMapper.fromSwitchPowerResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> PatchSwitchPowerResult.Error(CommonError.UserNotFound)
                else -> PatchSwitchPowerResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            PatchSwitchPowerResult.Error(CommonError.NetworkError)
        }
    }


}