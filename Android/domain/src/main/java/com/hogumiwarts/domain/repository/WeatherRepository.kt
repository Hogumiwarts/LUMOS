package com.hogumiwarts.domain.repository

import com.hogumiwarts.domain.model.ApiResult
import com.hogumiwarts.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getWeatherInfo(latitude: Double, longitude: Double): Flow<ApiResult<WeatherInfo>>
}