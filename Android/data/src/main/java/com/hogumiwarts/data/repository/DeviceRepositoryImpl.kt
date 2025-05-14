package com.hogumiwarts.data.repository

import com.hogumiwarts.data.entity.remote.Response.DeviceResponse
import com.hogumiwarts.data.mapper.toDomain
import com.hogumiwarts.data.source.remote.DeviceApi
import com.hogumiwarts.domain.model.DeviceResult
import com.hogumiwarts.domain.repository.DeviceRepository
import kotlinx.coroutines.flow.first
import timber.log.Timber
import javax.inject.Inject

class DeviceRepositoryImpl @Inject constructor(
    private val deviceApi: DeviceApi
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
}