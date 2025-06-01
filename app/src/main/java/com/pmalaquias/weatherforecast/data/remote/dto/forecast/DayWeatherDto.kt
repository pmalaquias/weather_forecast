package com.pmalaquias.weatherforecast.data.remote.dto.forecast

import com.google.gson.annotations.SerializedName
import com.pmalaquias.weatherforecast.data.remote.dto.ConditionDto

/**
 * Data Transfer Object representing the weather conditions for a single day.
 *
 * @property maxTempC Maximum temperature in Celsius.
 * @property minTempC Minimum temperature in Celsius.
 * @property avgTempC Average temperature in Celsius.
 * @property maxTempF Maximum temperature in Fahrenheit.
 * @property minTempF Minimum temperature in Fahrenheit.
 * @property avgTempF Average temperature in Fahrenheit.
 * @property maxWindMph Maximum wind speed in miles per hour.
 * @property maxWindKph Maximum wind speed in kilometers per hour.
 * @property totalPrecipMm Total precipitation in millimeters.
 * @property totalSnowCm Total snowfall in centimeters.
 * @property avgVisKm Average visibility in kilometers.
 * @property avgHumidity Average humidity percentage.
 * @property uvIndex UV index value.
 * @property dailyChanceOfRain Daily chance of rain as a percentage.
 * @property dailyChanceOfSnow Daily chance of snow as a percentage.
 * @property condition Weather condition details.
 * @property dailyWillItRain Indicates if it will rain (1) or not (0) during the day.
 * @property dailyWillItSnow Indicates if it will snow (1) or not (0) during the day.
 */
data class DayWeatherDto (
    @SerializedName("maxtemp_c") val maxTempC: Double,
    @SerializedName("mintemp_c") val minTempC: Double,
    @SerializedName("avgtemp_c") val avgTempC: Double,
    @SerializedName("maxtemp_f") val maxTempF: Double,
    @SerializedName("mintemp_f") val minTempF: Double,
    @SerializedName("avgtemp_f") val avgTempF: Double,
    @SerializedName("maxwind_mph") val maxWindMph: Double,
    @SerializedName("maxwind_kph") val maxWindKph: Double,
    @SerializedName("totalprecip_mm") val totalPrecipMm: Double,
    @SerializedName("totalsnow_cm") val totalSnowCm: Double,
    @SerializedName ("avgvis_km") val avgVisKm: Double,
    @SerializedName ("avghumidity") val avgHumidity: Double,
    @SerializedName ("uv") val uvIndex: Double,
    @SerializedName ("daily_chance_of_rain") val dailyChanceOfRain: Double,
    @SerializedName ("daily_chance_of_snow") val dailyChanceOfSnow: Double,
    @SerializedName("condition") val condition: ConditionDto,
    @SerializedName("daily_will_it_rain") val dailyWillItRain: Int,
    @SerializedName("daily_will_it_snow") val dailyWillItSnow: Int
)