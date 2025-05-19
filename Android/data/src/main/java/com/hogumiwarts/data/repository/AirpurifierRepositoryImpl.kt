package com.hogumiwarts.data.repository

import android.util.Log
import com.hogumiwarts.data.entity.remote.Request.FanModeRequest
import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.mapper.AirpurifierMapper
import com.hogumiwarts.data.source.remote.AirpurifierApi
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.airpurifier.AirpurifierResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierFanModeResult
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult
import com.hogumiwarts.domain.repository.AirpurifierRepository
import javax.inject.Inject

class AirpurifierRepositoryImpl @Inject constructor(
    private val airpurifierApi: AirpurifierApi // 🔹 Retrofit API 인터페이스 주입
) : AirpurifierRepository {
    override suspend fun getAirpurifierStatus(deviceId: Long): AirpurifierResult {
        return try {
            val response = airpurifierApi.getAirpurifierStatus(deviceId)
            val body = response.data

            if (body != null) {
                AirpurifierResult.Success(
                    data = AirpurifierMapper.fromAirpurifierDataResponse(body)
                )
            } else {
                AirpurifierResult.Error(CommonError.UnknownError)
            }

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> AirpurifierResult.Error(CommonError.UserNotFound)
                else -> AirpurifierResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            AirpurifierResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchAirpurifierPower(
        deviceId: Long,
        activated: Boolean,
    ): PatchAirpurifierPowerResult {
        return try {
            val response = airpurifierApi.patchAirpurifierPower(deviceId, PowerRequest(activated))
            val body = response.data

            if (body != null) {
                PatchAirpurifierPowerResult.Success(
                    data = AirpurifierMapper.fromPowerResponse(body)
                )
            } else {
                PatchAirpurifierPowerResult.Error(CommonError.UnknownError)
            }

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> PatchAirpurifierPowerResult.Error(CommonError.UserNotFound)
                else -> PatchAirpurifierPowerResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            PatchAirpurifierPowerResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchAirpurifierFanMode(
        deviceId: Long,
        fanMode: String,
    ): PatchAirpurifierFanModeResult {
        return try {
            val response = airpurifierApi.patchAirpurifierFanMode(deviceId, FanModeRequest(fanMode))
            val body = response.data

            if (body != null) {
                PatchAirpurifierFanModeResult.Success(
                    data = AirpurifierMapper.fromPowerResponse(body)
                )
            } else {
                PatchAirpurifierFanModeResult.Error(CommonError.UnknownError)
            }

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> PatchAirpurifierFanModeResult.Error(CommonError.UserNotFound)
                else -> PatchAirpurifierFanModeResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            PatchAirpurifierFanModeResult.Error(CommonError.NetworkError)
        }
    }
}