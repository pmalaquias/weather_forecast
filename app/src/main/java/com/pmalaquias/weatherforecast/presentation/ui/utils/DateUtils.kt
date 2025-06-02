package com.pmalaquias.weatherforecast.presentation.ui.utils

import android.util.Log
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {

    private val apiDateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun formatDisplayDate(dateString: String, locale: Locale = Locale.getDefault()): String {
        try {
            val forecastDate = LocalDate.parse(dateString, apiDateFormatter)
            val today = LocalDate.now()
            val tomorrow = today.plusDays(1)

            return when (forecastDate) {
                today -> "Hoje"
                tomorrow -> "Amanhã "
                else -> {
                    // Format: "Day of Week, Day of Month" (e.g., "Tue, 3 de June")
                    // Or just "Day of Month" (e.g., "3 de June")
                    val dayOfWeek = forecastDate.dayOfWeek.getDisplayName(TextStyle.SHORT, locale) // e.g., "Mon", "Tue"
                    val dayOfMonth = forecastDate.dayOfMonth
                    val month = forecastDate.month.getDisplayName(TextStyle.FULL, locale) // e.g., "June", "July"
                    // "$dayOfWeek, $dayOfMonth de $month" // More complete example
                    //"$dayOfMonth de $month"
                    "$dayOfWeek" 
                }
            }
        } catch (e: Exception) {
            // In case of parsing error, return the original string or a placeholder
            Log.e("DateUtils", "Error parsing date: $dateString", e)
            return dateString // Or some way to indicate error
        }
    }

    // Function to get only the abbreviated day of the week (e.g., "MON")
    fun getDayOfWeekAbbreviated(dateString: String, locale: Locale = Locale.getDefault()): String {
        try {
            val apiDateFormatter = null
            val forecastDate = LocalDate.parse(dateString, apiDateFormatter)
            return forecastDate.dayOfWeek.getDisplayName(TextStyle.SHORT, locale).uppercase(locale)
        } catch (e: Exception) {
            return "" // Or a placeholder
        }
    }
}