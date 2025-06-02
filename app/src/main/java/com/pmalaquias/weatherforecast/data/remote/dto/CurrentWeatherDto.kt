package com.pmalaquias.weatherforecast.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object representing the current weather information.
 *
 * @property tempCelcius Current temperature in Celsius.
 * @property tempFahrenheit Current temperature in Fahrenheit.
 * @property condition Weather condition details.
 * @property windKph Wind speed in kilometers per hour.
 * @property humidity Current humidity percentage.
 * @property feelslikeCelcius Perceived temperature in Celsius.
 * @property uvIndex Current UV index.
 * @property isDay Indicates if it is day (1) or night (0).
 */
data class CurrentWeatherDto(
    @SerializedName("temp_c") val tempCelcius: Double,
    @SerializedName("temp_f") val tempFahrenheit: Double,
    @SerializedName("condition") val condition: ConditionDto,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName ("wind_dir") val windDir: String,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("feelslike_c") val feelslikeCelcius: Double,
    @SerializedName("uv") val uvIndex: Double,
    @SerializedName("is_day") val isDay: Int, // 1 for day, 0 for night
    @SerializedName("pressure_mb") val pressureMb: Double,
    @SerializedName("precip_mm") val precipitationMm: Double
)

