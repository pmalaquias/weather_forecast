package com.pmalaquias.weatherforecast.domain.models

/**
 * Represents the weather condition with a descriptive text and an associated icon URL.
 *
 * @property text A human-readable description of the weather condition (e.g., "Sunny", "Cloudy").
 * @property iconUrl The URL of the icon representing the weather condition.
 */
data class WeatherCondition(
    val text: String,
    val iconUrl: String,
    val code: Int
)
