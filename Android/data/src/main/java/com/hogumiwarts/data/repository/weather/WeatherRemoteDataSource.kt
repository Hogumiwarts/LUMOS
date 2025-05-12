package com.hogumiwarts.data.repository.weather

import com.hogumiwarts.data.entity.remote.OpenWeatherResponse
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getWeatherInfo(
        latitude: Double,
        longitude: Double,
        apiKey: String
    ): Response<OpenWeatherResponse>
}