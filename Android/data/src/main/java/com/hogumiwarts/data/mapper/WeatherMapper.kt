package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.OpenWeatherResponse
import com.hogumiwarts.domain.model.WeatherInfo

object WeatherMapper {
    operator fun invoke(response: OpenWeatherResponse): WeatherInfo {
        val airQualityText = when (response.clouds.all) {
            in 0..20 -> "좋음"
            in 21..50 -> "보통"
            in 51..80 -> "나쁨"
            else -> "매우 나쁨"
        }

        val rainProbability = if (response.rain != null) {
            (response.rain.`1h`?.times(100) ?: 0f).toInt().coerceAtMost(100)
        } else {
            (response.clouds.all / 5).coerceAtMost(100) // 구름량을 기반으로 대략적 계산
        }

        return WeatherInfo(
            cityName = response.name,
            currentTemp = response.main.temp.toInt(),
            minTemp = response.main.temp_min.toInt(),
            maxTemp = response.main.temp_max.toInt(),
            airQuality = airQualityText,
            rainProbability = rainProbability,
            humidity = response.main.humidity,
            weatherIcon = response.weather.firstOrNull()?.icon ?: ""
        )
    }
}