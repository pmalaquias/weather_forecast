package com.pmalaquias.weatherforecast.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing weather condition information received from the remote API.
 *
 * @property text The textual description of the weather condition (e.g., "Sunny", "Cloudy").
 * @property iconUrl The URL of the icon representing the weather condition.
 *                  Example: "//cdn.weatherapi.com/weather/64x64/day/113.png"
 * @property code The unique code identifying the specific weather condition.
 */
data class ConditionDto(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val iconUrl: String, 
    @SerializedName("code") val code: Int
)
