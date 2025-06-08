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
    @SerializedName("temp_f") val tempFahrenheit: Double?, // Pode ser nulável
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("condition") val condition: ConditionDto,
    @SerializedName("wind_mph") val windMph: Double?,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_degree") val windDegree: Int?,
    @SerializedName("wind_dir") val windDir: String?, // Nulável se a API pode não enviar
    @SerializedName("pressure_mb") val pressureMb: Double?, // Nulável
    @SerializedName("pressure_in") val pressureIn: Double?,
    @SerializedName("precip_mm") val precipitationMm: Double?, // Nulável
    @SerializedName("precip_in") val precipitationIn: Double?,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("cloud") val cloud: Int?,
    @SerializedName("feelslike_c") val feelslikeCelcius: Double,
    @SerializedName("feelslike_f") val feelslikeFahrenheit: Double?,
    @SerializedName("vis_km") val visKm: Double?,
    @SerializedName("vis_miles") val visMiles: Double?,
    @SerializedName("uv") val uvIndex: Double, // API usa "uv" para o índice UV
    @SerializedName("gust_mph") val gustMph: Double?,
    @SerializedName("gust_kph") val gustKph: Double?
)

