package com.hogumiwarts.data.mapper

import com.hogumiwarts.data.entity.remote.OpenWeatherResponse
import com.hogumiwarts.domain.model.WeatherInfo
import kotlin.math.roundToInt

object WeatherMapper {

    fun getWeatherImageKey(weatherId: Int): String = when (weatherId) {
        in 200..232 -> "storm"
        in 300..531 -> "rain"
        in 600..622 -> "snow"
        in 701..781 -> "wind"
        800 -> "clear"
        in 801..804 -> "clouds"
        else -> "clouds"
    }

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
            currentTemp = response.main.temp.roundToInt(),
            minTemp = response.main.temp_min.roundToInt(),
            maxTemp = response.main.temp_max.roundToInt(),
            airQuality = airQualityText,
            rainProbability = rainProbability,
            humidity = response.main.humidity,
            weatherIcon = getWeatherImageKey(response.weather.firstOrNull()?.id ?: 800)
        )
    }
}