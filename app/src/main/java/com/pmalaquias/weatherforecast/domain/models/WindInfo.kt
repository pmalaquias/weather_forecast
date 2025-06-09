package com.pmalaquias.weatherforecast.domain.models

/**
 * Represents information about wind conditions.
 *
 * @property speed The speed of the wind, typically measured in meters per second (m/s).
 * @property direction The cardinal direction from which the wind is blowing (e.g., "N", "NE", "E").
 */
data class WindInfo(
    val speed: Double,
    val direction: String
)
