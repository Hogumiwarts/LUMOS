package com.hogumiwarts.data.repository.weather

import android.util.Log
import com.hogumiwarts.data.mapper.WeatherMapper
import com.hogumiwarts.data.source.remote.WeatherApi
import com.hogumiwarts.domain.model.ApiResult
import com.hogumiwarts.domain.model.WeatherInfo
import com.hogumiwarts.domain.repository.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
) : WeatherRepository {

    private val apiKey = "a1d376dacea857fa6c707b160153db11"

    override suspend fun getWeatherInfo(latitude: Double, longitude: Double): Flow<ApiResult<WeatherInfo>> = flow {
        emit(ApiResult.loading(null))
        try {
            val response = withContext(CoroutineScope(Dispatchers.IO).coroutineContext) {
                weatherRemoteDataSource.getWeatherInfo(latitude, longitude, apiKey)
            }

            val responseBody = response.body()
            Timber.tag("WeatherRepositoryImpl").d("Response: $responseBody")

            if (response.isSuccessful && responseBody != null) {
                Timber.tag("WeatherRepositoryImpl").d("getWeatherInfo Success")
                emit(ApiResult.success(WeatherMapper(responseBody)))
            } else {
                Timber.tag("WeatherRepositoryImpl").d("getWeatherInfo Fail: ${response.code()}")
                emit(ApiResult.error(response.errorBody().toString(), null))
            }
        } catch (e: Exception) {
            Timber.tag("WeatherRepositoryImpl").e("getWeatherInfo Error: $e")
            emit(ApiResult.fail())
        }
    }
}