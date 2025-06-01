package com.pmalaquias.weatherforecast.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName
import com.pmalaquias.weatherforecast.data.remote.dto.ConditionDto

/**
 * Data Transfer Object representing the weather forecast for a specific hour of the day.
 *
 * @property timeEpoch The time of the forecast in epoch seconds.
 * @property time The time of the forecast as a formatted string.
 * @property tempC Temperature in degrees Celsius.
 * @property tempF Temperature in degrees Fahrenheit.
 * @property condition Weather condition details.
 * @property windMph Wind speed in miles per hour.
 * @property windKph Wind speed in kilometers per hour.
 * @property windDir Wind direction as a string (e.g., "NW").
 * @property pressureMb Atmospheric pressure in millibars.
 * @property precipMm Precipitation amount in millimeters.
 * @property precipIn Precipitation amount in inches.
 * @property snowCm Snow amount in centimeters.
 * @property humidity Relative humidity as a percentage.
 * @property cloud Cloud cover as a percentage.
 * @property feelslikeC Feels-like temperature in degrees Celsius.
 * @property feelslikeF Feels-like temperature in degrees Fahrenheit.
 * @property windchillC Wind chill temperature in degrees Celsius.
 * @property windchillF Wind chill temperature in degrees Fahrenheit.
 * @property heatindexC Heat index in degrees Celsius.
 * @property heatindexF Heat index in degrees Fahrenheit.
 * @property dewpointC Dew point temperature in degrees Celsius.
 * @property dewpointF Dew point temperature in degrees Fahrenheit.
 * @property dailyWillItRain Indicates if it will rain during the day (1 for yes, 0 for no).
 * @property dailyWillItSnow Indicates if it will snow during the day (1 for yes, 0 for no).
 * @property isDay Indicates if the time is during the day (1 for day, 0 for night).
 * @property visKm Visibility in kilometers.
 * @property visMiles Visibility in miles.
 * @property chanceOfRain Probability of rain as a percentage.
 * @property chanceOfSnow Probability of snow as a percentage.
 * @property gustMph Wind gust speed in miles per hour.
 * @property gustKph Wind gust speed in kilometers per hour.
 * @property uvIndex UV index value.
 */
data class HourDayDto (
    @SerializedName("time_epoch") val timeEpoch: Long,
    @SerializedName("time") val time: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("temp_f") val tempF: Double,
    @SerializedName("condition") val condition: ConditionDto,
    @SerializedName("wind_mph") val windMph: Double,
    @SerializedName("wind_kph") val windKph: Double,
    @SerializedName("wind_dir") val windDir: String,
    @SerializedName("pressure_mb") val pressureMb: Double,
    @SerializedName("precip_mm") val precipMm: Double,
    @SerializedName("precip_in") val precipIn: Double,
    @SerializedName("snow_cm") val snowCm: Double,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("cloud") val cloud: Int,
    @SerializedName("feelslike_c") val feelslikeC: Double,
    @SerializedName("feelslike_f") val feelslikeF: Double,
    @SerializedName("windchill_c") val windchillC: Double,
    @SerializedName("windchill_f") val windchillF: Double,
    @SerializedName("heatindex_c") val heatindexC: Double,
    @SerializedName("heatindex_f") val heatindexF: Double,
    @SerializedName("dewpoint_c") val dewpointC: Double,
    @SerializedName("dewpoint_f") val dewpointF: Double,
    @SerializedName("daily_will_it_rain") val dailyWillItRain: Int,
    @SerializedName("daily_will_it_snow") val dailyWillItSnow: Int,
    @SerializedName("is_day") val isDay: Int, // 1 for day, 0 for night
    @SerializedName("vis_km") val visKm: Double,
    @SerializedName("vis_miles") val visMiles: Double,
    @SerializedName("chance_of_rain") val chanceOfRain: Int,
    @SerializedName("chance_of_snow") val chanceOfSnow: Int,
    @SerializedName("gust_mph") val gustMph: Double,
    @SerializedName("gust_kph") val gustKph: Double,
    @SerializedName ("uv") val uvIndex: Double
)