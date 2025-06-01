package com.pmalaquias.weatherforecast.domain.models

/**
 * Represents the overall forecast data, typically containing a list of daily forecasts.
 *
 * @property dailyForecasts A list of [DailyForecast] objects, each detailing the weather
 * for a specific day.
 */
data class ForecastData(
    val dailyForecasts: List<DailyForecast>
)