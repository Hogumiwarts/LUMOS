package com.hogumiwarts.lumos.utils

import com.hogumiwarts.lumos.R
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object CommonUtils {

    fun getFormattedToday(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE, MMMM dd", Locale.ENGLISH)
        return today.format(formatter)
    }

    fun getWeatherIconRes(weatherKey: String): Int {
        // 현재 시각의 시(hour)만 꺼냅니다.
        val hour = LocalTime.now().hour
        val isDay = hour in 6..17

        return when (weatherKey) {
            "storm"  -> if (isDay) R.drawable.ic_sun_storm  else R.drawable.ic_night_storm
            "rain"   -> if (isDay) R.drawable.ic_sun_rain   else R.drawable.ic_night_rain
            "snow"   -> if (isDay) R.drawable.ic_sun_snow   else R.drawable.ic_night_snow
            "wind"   -> if (isDay) R.drawable.ic_sun_wind   else R.drawable.ic_night_wind
            "clear"  -> if (isDay) R.drawable.ic_sun_clear  else R.drawable.ic_night_clear
            "clouds" -> if (isDay) R.drawable.ic_sun_clouds else R.drawable.ic_night_clouds
            else     -> if (isDay) R.drawable.ic_sun_clouds else R.drawable.ic_night_clouds
        }
    }

}