package com.hogumiwarts.data.repository

import android.util.Log
import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.mapper.DeviceMapper
import com.hogumiwarts.data.mapper.SwitchMapper
import com.hogumiwarts.data.source.remote.SwitchApi
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.PatchSwitchPowerResult
import com.hogumiwarts.domain.model.minibig.GetSwitchStatusResult
import com.hogumiwarts.domain.repository.SwitchRepository
import javax.inject.Inject

class SwitchRepositoryImpl @Inject constructor(
    private val switchApi: SwitchApi // 🔹 Retrofit API 인터페이스 주입
) : SwitchRepository {

    // 🔸 기기 데이터를 API로부터 받아오는 함수
    override suspend fun getSwitchStatus(deviceId: Long): GetSwitchStatusResult {
        return try {
            val response = switchApi.getSwitchStatus(deviceId)
            val data = response.data ?: return GetSwitchStatusResult.Error(CommonError.UnknownError)

            GetSwitchStatusResult.Success(
                data = SwitchMapper.fromSwitchStatusDataResponse(data)
            )
        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> GetSwitchStatusResult.Error(CommonError.UserNotFound)
                else -> GetSwitchStatusResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            GetSwitchStatusResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchSwitchPower(
        deviceId: Long,
        activated: Boolean
    ): PatchSwitchPowerResult {
        return try {
            val response = switchApi.getSwitchPower(deviceId, PowerRequest(activated))
            val data =
                response.data ?: return PatchSwitchPowerResult.Error(CommonError.UnknownError)

            PatchSwitchPowerResult.Success(
                data = DeviceMapper.fromSwitchPowerResponse(data)
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
}