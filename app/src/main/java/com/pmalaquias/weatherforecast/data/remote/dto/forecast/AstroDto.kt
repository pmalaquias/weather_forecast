package com.pmalaquias.weatherforecast.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing astronomical information for a specific day.
 *
 * @property sunrise The time of sunrise.
 * @property sunset The time of sunset.
 * @property moonrise The time of moonrise.
 * @property moonset The time of moonset.
 * @property moonPhase The current phase of the moon.
 * @property moonIllumination The percentage of the moon illuminated.
 * @property isSunUp Indicates if the sun is currently above the horizon.
 * @property isMoonUp Indicates if the moon is currently above the horizon.
 */
data class AstroDto (
    @SerializedName("sunrise") val sunrise: String,
    @SerializedName("sunset") val sunset: String,
    @SerializedName("moonrise") val moonrise: String,
    @SerializedName("moonset") val moonset: String,
    @SerializedName("moon_phase") val moonPhase: String,
    @SerializedName("moon_illumination") val moonIllumination: String,
    @SerializedName("is_sun_up") val isSunUp: Int,
    @SerializedName("is_moon_up") val isMoonUp: Int
)