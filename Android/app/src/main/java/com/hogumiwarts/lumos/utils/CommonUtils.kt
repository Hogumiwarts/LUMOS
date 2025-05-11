package com.hogumiwarts.lumos.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object CommonUtils {

    fun getFormattedToday(): String {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE, MMMM dd", Locale.ENGLISH)
        return today.format(formatter)
    }

}