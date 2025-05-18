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

class SwitchRepositoryImpl@Inject constructor(
    private val switchApi: SwitchApi // 🔹 Retrofit API 인터페이스 주입
) : SwitchRepository {

    // 🔸 기기 데이터를 API로부터 받아오는 함수
    override suspend fun getSwitchStatus(deviceId: Int): GetSwitchStatusResult {
        return try {
            // ✅ API 호출
            val response = switchApi.getSwitchStatus(deviceId)

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            GetSwitchStatusResult.Success(
                data = SwitchMapper.fromSwitchStatusDataResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> GetSwitchStatusResult.Error(CommonError.UserNotFound)
                else -> GetSwitchStatusResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            GetSwitchStatusResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchSwitchPower(deviceId: Int, activated: Boolean): PatchSwitchPowerResult {
        return try {
            // ✅ API 호출
            val response = switchApi.getSwitchPower(deviceId, PowerRequest(activated))

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