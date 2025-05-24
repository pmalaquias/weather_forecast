package com.pmalaquias.weatherforecast.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data transfer object (DTO) representing the response from the weather API.
 *
 * @property location The location details of the weather data.
 * @property current The current weather details.
 */
data class WeatherApiResponseDto(
    @SerializedName("location") val location: LocationDto,
    @SerializedName("current") val current: CurrentWeatherDto
)
