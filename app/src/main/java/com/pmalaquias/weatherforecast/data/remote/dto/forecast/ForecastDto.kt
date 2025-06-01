package com.pmalaquias.weatherforecast.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing the forecast data received from the remote API.
 *
 * @property forecastDay A list of [ForecastDayDto] objects, each representing the weather forecast for a specific day.
 */
data class ForecastDto (
    @SerializedName("forecastday") val forecastDay: List<ForecastDayDto>

)