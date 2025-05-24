package com.pmalaquias.weatherforecast.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing a location as received from the remote API.
 *
 * @property name The name of the location (e.g., city or town).
 * @property region The region or state where the location is situated.
 * @property country The country of the location.
 * @property lat The latitude coordinate of the location.
 * @property lon The longitude coordinate of the location.
 * @property tzId The time zone identifier for the location (e.g., "America/New_York").
 * @property localtimeEpoch The local time in epoch format (seconds since 1970-01-01).
 * @property localtime The local time at the location, formatted as a string.
 */
data class LocationDto(
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("country") val country: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lon") val lon: Double,
    @SerializedName("tz_id") val tzId: String,
    @SerializedName("localtime_epoch") val localtimeEpoch: Long,
    @SerializedName("localtime") val localtime: String
)
