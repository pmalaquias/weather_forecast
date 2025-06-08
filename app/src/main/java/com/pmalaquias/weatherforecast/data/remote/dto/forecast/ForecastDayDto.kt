package com.pmalaquias.weatherforecast.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing a forecast for a specific day.
 *
 * @property date The date of the forecast in yyyy-MM-dd format.
 * @property dateEpoch The date of the forecast as a Unix epoch timestamp.
 * @property day The weather details for the day.
 * @property astro The astronomical data for the day (e.g., sunrise, sunset).
 * @property hour The list of hourly weather forecasts for the day.
 */
data class ForecastDayDto (
    @SerializedName("date") val date: String,
    @SerializedName("date_epoch") val dateEpoch: Long,
    @SerializedName("day") val day: DayDetailsDto,
    @SerializedName("astro") val astro: AstroDto,
    @SerializedName("hour") val hour: List<HourDayDto>?
)