package com.hogumiwarts.data.entity.remote

data class OpenWeatherResponse(
    val name: String,
    val main: MainData,
    val weather: List<WeatherItem>,
    val clouds: CloudsData,
    val rain: RainData? = null
)

data class MainData(
    val temp: Float,
    val temp_min: Float,
    val temp_max: Float,
    val humidity: Int
)

data class WeatherItem(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class CloudsData(val all: Int)
data class RainData(val `1h`: Float? = 0f)