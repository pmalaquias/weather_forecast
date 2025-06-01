package com.pmalaquias.weatherforecast.domain.models

/**
 * Represents information about a specific location.
 *
 * @property name The name of the location (e.g., city or town).
 * @property region The region or state where the location is situated.
 * @property country The country of the location.
 * @property localtime The local time at the location, typically in a formatted string.
 */
data class LocationInfo(
    val name: String,
    val region: String,
    val country: String,
    val localtime: String,
    val timezoneId: String,
    val lat: Double,
    val lon: Double
)
