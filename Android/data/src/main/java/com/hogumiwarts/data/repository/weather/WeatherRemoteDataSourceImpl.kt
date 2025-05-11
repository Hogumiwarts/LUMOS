package com.hogumiwarts.data.repository.weather

import com.hogumiwarts.data.entity.remote.OpenWeatherResponse
import com.hogumiwarts.data.source.remote.WeatherApi
import retrofit2.Response
import javax.inject.Inject

class WeatherRemoteDataSourceImpl @Inject constructor(
    private val weatherApi: WeatherApi
) : WeatherRemoteDataSource {
    override suspend fun getWeatherInfo(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Response<OpenWeatherResponse> {
        return weatherApi.getCurrentWeather(latitude, longitude, apiKey)
    }
}