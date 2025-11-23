package com.pmalaquias.weatherforecast.domain.models

/**
 * Represents the weather data for a specific location.
 *
 * @property location Information about the location for which the weather data is provided.
 * @property current The current weather conditions at the specified location.
 */
data class WeatherData(
    val location: LocationInfo,
    val isFromCurrentLocation: Boolean = false,
    val current: CurrentWeather
)
