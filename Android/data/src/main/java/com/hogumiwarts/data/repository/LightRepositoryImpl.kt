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

class LightRepositoryImpl @Inject constructor(
    private val lightApi: LightApi // 🔹 Retrofit API 인터페이스 주입
) : LightRepository {

    // 🔸 기기 데이터를 API로부터 받아오는 함수
    override suspend fun getLightStatus(deviceId: Long): GetLightStatusResult {
        return try {
            val response = lightApi.getLightStatus(deviceId)
            val data = response.data ?: return GetLightStatusResult.Error(CommonError.UnknownError)

            Log.d("TAG", "getSwitchStatus: $response")

            GetLightStatusResult.Success(
                data = LightMapper.fromSwitchStatusDataResponse(data)
            )

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> GetLightStatusResult.Error(CommonError.UserNotFound)
                else -> GetLightStatusResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            GetLightStatusResult.Error(CommonError.NetworkError)
        }
    }


    override suspend fun patchLightPower(
        deviceId: Long,
        activated: Boolean
    ): PatchSwitchPowerResult {
        return try {
            val response = lightApi.patchLightPower(deviceId, PowerRequest(activated))
            val data =
                response.data ?: return PatchSwitchPowerResult.Error(CommonError.UnknownError)

            PatchSwitchPowerResult.Success(
                data = LightMapper.fromSwitchPowerResponse(data)
            )
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> PatchSwitchPowerResult.Error(CommonError.UserNotFound)
                else -> PatchSwitchPowerResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            PatchSwitchPowerResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchLightBright(deviceId: Long, brightness: Int): LightBrightResult {
        return try {
            val response = lightApi.patchLightBright(deviceId, PatchLightBrightRequest(brightness))
            val data = response.data ?: return LightBrightResult.Error(CommonError.UnknownError)

            LightBrightResult.Success(
                data = LightMapper.fromLightBrightResponse(data)
            )
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> LightBrightResult.Error(CommonError.UserNotFound)
                else -> LightBrightResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            LightBrightResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchLightColor(
        deviceId: Long,
        color: Float,
        saturation: Float
    ): LightColorResult {
        return try {
            val response =
                lightApi.patchLightColor(deviceId, PatchLightColorRequest(color, saturation))
            val data = response.data ?: return LightColorResult.Error(CommonError.UnknownError)

            LightColorResult.Success(
                data = LightMapper.fromLightColorResponse(data)
            )
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> LightColorResult.Error(CommonError.UserNotFound)
                else -> LightColorResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            LightColorResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchLightTemperature(
        deviceId: Long,
        temperature: Int
    ): LightTemperatureResult {
        return try {
            val response =
                lightApi.patchLightTemperature(deviceId, LightTemperatureRequest(temperature))
            val data =
                response.data ?: return LightTemperatureResult.Error(CommonError.UnknownError)

            LightTemperatureResult.Success(
                data = LightMapper.fromLightTemperatureResponse(data)
            )
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> LightTemperatureResult.Error(CommonError.UserNotFound)
                else -> LightTemperatureResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            LightTemperatureResult.Error(CommonError.NetworkError)
        }
    }


}