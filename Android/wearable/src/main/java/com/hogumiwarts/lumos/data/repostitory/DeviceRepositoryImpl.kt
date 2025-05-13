package com.hogumiwarts.lumos.data.repostitory

import android.util.Log
import com.hogumiwarts.lumos.data.entity.mapper.DeviceMapper
import com.hogumiwarts.lumos.data.source.remote.DevicesApi
import com.hogumiwarts.lumos.domain.model.CommonError
import com.hogumiwarts.lumos.domain.model.GetDevicesResult
import com.hogumiwarts.lumos.domain.repository.DeviceRepository
import javax.inject.Inject

// 🔹 실제 데이터를 가져오는 Repository 구현체
class DeviceRepositoryImpl @Inject constructor(
    private val devicesApi: DevicesApi // 🔹 Retrofit API 인터페이스 주입
) : DeviceRepository {

    // 🔸 기기 데이터를 API로부터 받아오는 함수
    override suspend fun getDevices(): GetDevicesResult {
        return try {
            // ✅ API 호출
            val response = devicesApi.getGestureList()

            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            GetDevicesResult.Success(
                data = DeviceMapper.fromDeviceListDataResponseList(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> GetDevicesResult.Error(CommonError.UserNotFound)
                else -> GetDevicesResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            GetDevicesResult.Error(CommonError.NetworkError)
        }
    }
}
