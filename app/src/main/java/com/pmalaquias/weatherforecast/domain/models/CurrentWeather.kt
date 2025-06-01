package com.pmalaquias.weatherforecast.domain.models

/**
 * Represents the current weather conditions.
 *
 * @property tempCelcius The current temperature in Celsius.
 * @property condition The current weather condition.
 * @property windKph The wind speed in kilometers per hour.
 * @property humidity The current humidity percentage.
 * @property feelslikeCelcius The perceived temperature in Celsius.
 * @property isDay Indicator if it is day (1) or night (0).
 */
data class CurrentWeather(
    val tempCelcius: Double,
    val condition: WeatherCondition,
    val windKph: Double,
    val windDir: String,
    val uv: Int,
    val humidity: Int,
    val feelslikeCelcius: Double,
    val isDay: Int,
    val pressureMb: Double,
    val precipitationMm: Double
)
