package com.hogumiwarts.domain.usecase

import com.hogumiwarts.domain.model.WeatherInfo
import com.hogumiwarts.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
){
    suspend operator fun invoke(latitude: Double, longitude: Double): Flow<Result<WeatherInfo>> {
        return weatherRepository.getWeatherInfo(latitude, longitude)
    }
}