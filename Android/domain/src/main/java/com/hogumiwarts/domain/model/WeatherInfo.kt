package com.hogumiwarts.domain.model

data class WeatherInfo(
    val cityName: String,
    val currentTemp: Int,
    val minTemp: Int,
    val maxTemp: Int,
    val airQuality: String,
    val rainProbability: Int,
    val humidity: Int,
    val weatherIcon: String
)
