package com.hogumiwarts.data.repository

import com.hogumiwarts.data.entity.remote.Response.DeviceDiscoverResponse
import com.hogumiwarts.data.entity.remote.Response.DeviceResponse
import com.hogumiwarts.data.mapper.DeviceMapper
import com.hogumiwarts.data.mapper.toDomain
import com.hogumiwarts.data.source.remote.DeviceApi
import com.hogumiwarts.data.source.remote.WearableDevicesApi
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.model.devices.GetDevicesResult
import com.hogumiwarts.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceApi: DeviceApi,
    private val devicesApi: WearableDevicesApi
) : DeviceRepository {

    // DB에서 기기 목록 불러오기
    override suspend fun getDevicesFromServer(accessToken: String): List<DeviceResult> {
        return try {
            val response = deviceApi.getMyDevices("Bearer $accessToken")
            response.data.map { it.toDomain() }
        } catch (e: retrofit2.HttpException) {
            Timber.e(e, "❌ HTTP 오류 발생 (code=${e.code()}, message=${e.message()})")
            emptyList()
        } catch (e: Exception) {
            Timber.e(e, "❌ 기타 오류 발생: ${e.message}")
            emptyList()
        }
    }

    // 기기 목록 새로고침
    override suspend fun discoverDevices(
        accessToken: String,
        installedAppId: String
    ): List<DeviceResult> {
        return try {
            val response = deviceApi.discover("Bearer $accessToken", installedAppId)
            response.data.map { it.toDomain() }
        } catch (e: retrofit2.HttpException) {
            Timber.e(e, "❌ HTTP 오류 발생 (code=${e.code()}, message=${e.message()})")
            emptyList()
        } catch (e: Exception) {
            Timber.e(e, "❌ 기타 오류 발생: ${e.message}")
            emptyList()
        }
    }

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