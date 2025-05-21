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

class AudioRepositoryImpl @Inject constructor(
    private val audioApi: AudioApi // 🔹 Retrofit API 인터페이스 주입
) : AudioRepository {
    override suspend fun getAudioStatus(deviceId: Long): AudioStatusResult {
        return try {
            val response = audioApi.getAudioStatus(deviceId)
            val body = response.data

            if (body != null) {
                AudioStatusResult.Success(
                    data = AudioMapper.fromAudioStatusDataResponse(body)
                )
            } else {
                AudioStatusResult.Error(CommonError.UnknownError)
            }

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> AudioStatusResult.Error(CommonError.UserNotFound)
                else -> AudioStatusResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            AudioStatusResult.Error(CommonError.NetworkError)
        }
    }


    override suspend fun patchAudioPower(deviceId: Long, activated: Boolean): AudioPowerResult {
        return try {
            val response = audioApi.patchAudioPower(deviceId, PowerRequest(activated))
            val body = response.data

            if (body != null) {
                AudioPowerResult.Success(
                    data = AudioMapper.fromAudioPowerDataResponse(body)
                )
            } else {
                AudioPowerResult.Error(CommonError.UnknownError)
            }

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> AudioPowerResult.Error(CommonError.UserNotFound)
                else -> AudioPowerResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            AudioPowerResult.Error(CommonError.NetworkError)
        }
    }

    override suspend fun patchAudioVolume(deviceId: Long, volume: Int): AudioVolumeResult {
        return try {
            val response = audioApi.patchAudioVolume(deviceId, AudioVolumeRequest(volume))
            val body = response.data

            if (body != null) {
                AudioVolumeResult.Success(
                    data = AudioMapper.fromAudioVolumeDataResponse(body)
                )
            } else {
                AudioVolumeResult.Error(CommonError.UnknownError)
            }

        } catch (e: retrofit2.HttpException) {
            when (e.code()) {
                404 -> AudioVolumeResult.Error(CommonError.UserNotFound)
                else -> AudioVolumeResult.Error(CommonError.UnknownError)
            }
        } catch (e: Exception) {
            AudioVolumeResult.Error(CommonError.NetworkError)
        }
    }

}