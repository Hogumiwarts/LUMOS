package com.hogumiwarts.data.repository

import android.util.Log
import com.hogumiwarts.data.entity.remote.Request.LightTemperatureRequest
import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.entity.remote.Response.light.PatchLightBrightRequest
import com.hogumiwarts.data.entity.remote.Response.light.PatchLightColorRequest
import com.hogumiwarts.data.mapper.LightMapper
import com.hogumiwarts.data.source.remote.LightApi
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.light.GetLightStatusResult
import com.hogumiwarts.domain.model.light.LightBrightResult
import com.hogumiwarts.domain.model.light.LightColorResult
import com.hogumiwarts.domain.model.light.LightTemperatureResult
import com.hogumiwarts.domain.repository.LightRepository
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
            val response = lightApi.patchLightPower(deviceId, PowerRequest(activated))

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            PatchSwitchPowerResult.Success(
                data = LightMapper.fromSwitchPowerResponse(response.data)
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

    override suspend fun patchLightBright(deviceId: Long, brightness: Int): LightBrightResult {
        return try {
            // ✅ API 호출
            val response = lightApi.patchLightBright(deviceId, PatchLightBrightRequest(brightness))

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            LightBrightResult.Success(
                data = LightMapper.fromLightBrightResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> LightBrightResult.Error(CommonError.UserNotFound)
                else -> LightBrightResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            LightBrightResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchLightColor(deviceId: Long, color: Int,saturation: Float): LightColorResult {

        return try {
            // ✅ API 호출
            val response = lightApi.patchLightColor(deviceId, PatchLightColorRequest(color,100.0f))

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래
            LightColorResult.Success(
                data = LightMapper.fromLightColorResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> LightColorResult.Error(CommonError.UserNotFound)
                else -> LightColorResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            LightColorResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchLightTemperature(
        deviceId: Long,
        temperature: Int,
    ): LightTemperatureResult {
        return try {
            // ✅ API 호출
            val response = lightApi.patchLightTemperature(deviceId, LightTemperatureRequest(temperature))

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래
            LightTemperatureResult.Success(
                data = LightMapper.fromLightTemperatureResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> LightTemperatureResult.Error(CommonError.UserNotFound)
                else -> LightTemperatureResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            LightTemperatureResult.Error(CommonError.NetworkError)
        }
    }


}