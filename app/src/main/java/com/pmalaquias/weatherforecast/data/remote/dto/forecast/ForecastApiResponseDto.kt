package com.pmalaquias.weatherforecast.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName
import com.pmalaquias.weatherforecast.data.remote.dto.CurrentWeatherDto
import com.pmalaquias.weatherforecast.data.remote.dto.LocationDto

/**
 * Data Transfer Object representing the API response for a weather forecast.
 *
 * @property location The location information for the forecast.
 * @property current The current weather conditions.
 * @property forecast The forecast details for upcoming days.
 */
data class ForecastApiResponseDto(
    @SerializedName("location") val location: LocationDto,
    @SerializedName("current") val current: CurrentWeatherDto,
    @SerializedName("forecast") val forecast: ForecastDto
)