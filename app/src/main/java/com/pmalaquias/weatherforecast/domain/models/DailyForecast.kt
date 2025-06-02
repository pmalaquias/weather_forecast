package com.pmalaquias.weatherforecast.domain.models

/**
 * Represents the weather forecast for a single day.
 *
 * @property date The date of the forecast (e.g., "2023-10-27").
 * @property maxTempCelcius The maximum temperature in Celsius for the day.
 * @property minTempCelcius The minimum temperature in Celsius for the day.
 * @property avgTempCelcius The average temperature in Celsius for the day.
 * @property condition The overall weather condition for the day, including text and icon.
 * Reuses the [WeatherCondition] domain model.
 * @property sunriseTime The time of sunrise (e.g., "06:30 AM").
 * @property sunsetTime The time of sunset (e.g., "06:00 PM").
 * @property chanceOfRain The chance of rain as a percentage (e.g., 30 for 30%). Nullable if not always available.
 * @property totalPrecipMm Total precipitation in millimeters for the day.
 */
data class DailyForecast(
    val date: String,
    val maxTempCelcius: Double,
    val minTempCelcius: Double,
    val avgTempCelcius: Double, // Average temperature might be useful
    val condition: WeatherCondition, // Reusing your existing domain model
    val sunriseTime: String,
    val sunsetTime: String,
    val chanceOfRain: Int?, // Example: 30 for 30% chance. Nullable.
    val totalPrecipMm: Double, // Total precipitation
    val uvIndex: Double, // UV index for the day
    val humidity: Double, // Humidity percentage
    )