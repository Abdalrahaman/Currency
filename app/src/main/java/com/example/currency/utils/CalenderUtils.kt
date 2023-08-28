package com.example.currency.utils

import com.example.currency.utils.Constants.Companion.LAST_THREE_DAYS
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getLastThreeDaysDate(): String {
    // Get the current date
    val calendar = Calendar.getInstance()

    // Subtract 3 days
    calendar.add(Calendar.DAY_OF_MONTH, LAST_THREE_DAYS)

    // Format the date using SimpleDateFormat
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return dateFormat.format(calendar.time)
}