package com.hogumiwarts.data.repository

import android.util.Log
import com.hogumiwarts.data.entity.remote.Request.AudioVolumeRequest
import com.hogumiwarts.data.entity.remote.Request.PowerRequest
import com.hogumiwarts.data.mapper.AirpurifierMapper
import com.hogumiwarts.data.mapper.AudioMapper
import com.hogumiwarts.data.source.remote.AirpurifierApi
import com.hogumiwarts.data.source.remote.AudioApi
import com.hogumiwarts.domain.model.CommonError
import com.hogumiwarts.domain.model.airpurifier.PatchAirpurifierPowerResult
import com.hogumiwarts.domain.model.audio.AudioPowerResult
import com.hogumiwarts.domain.model.audio.AudioStatusResult
import com.hogumiwarts.domain.model.audio.AudioVolumeResult
import com.hogumiwarts.domain.repository.AirpurifierRepository
import com.hogumiwarts.domain.repository.AudioRepository
import javax.inject.Inject

class AudioRepositoryImpl@Inject constructor(
    private val audioApi: AudioApi // 🔹 Retrofit API 인터페이스 주입
) : AudioRepository{
    override suspend fun getAudioStatus(deviceId: Long): AudioStatusResult {
        return try {
            // ✅ API 호출
            val response = audioApi.getAudioStatus(deviceId)

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            AudioStatusResult.Success(
                data = AudioMapper.fromAudioStatusDataResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> AudioStatusResult.Error(CommonError.UserNotFound)
                else -> AudioStatusResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            AudioStatusResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchAudioPower(deviceId: Long, activated: Boolean): AudioPowerResult {

        return try {
            // ✅ API 호출
            val response = audioApi.patchAudioPower(deviceId, PowerRequest(activated))

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            AudioPowerResult.Success(
                data = AudioMapper.fromAudioPowerDataResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> AudioPowerResult.Error(CommonError.UserNotFound)
                else -> AudioPowerResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            AudioPowerResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchAudioVolume(deviceId: Long, volume: Int): AudioVolumeResult {
        return try {
            // ✅ API 호출
            val response = audioApi.patchAudioVolume(deviceId, AudioVolumeRequest(volume))

            Log.d("TAG", "getSwitchStatus: $response")
            // ✅ 응답 데이터 매핑 후 성공 결과로 래핑
            AudioVolumeResult.Success(
                data = AudioMapper.fromAudioVolumeDataResponse(response.data)
            )

        } catch (e: retrofit2.HttpException) {
            // 🔶 서버 에러 코드별 처리
            when (e.code()) {
                404 -> AudioVolumeResult.Error(CommonError.UserNotFound)
                else -> AudioVolumeResult.Error(CommonError.UnknownError)
            }

        } catch (e: Exception) {
            // 🔶 기타 네트워크/변환 등 예외 처리
            AudioVolumeResult.Error(CommonError.NetworkError)
        }
    }

}