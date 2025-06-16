package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import com.pmalaquias.weatherforecast.domain.models.ForecastData
import com.pmalaquias.weatherforecast.domain.models.WeatherData

/**
 * Represents the UI state for the weather screen.
 *
 * @property isInitialLoading Indicates whether the initial weather data is being loaded.
 * @property isRefreshing Indicates whether the weather data is being refreshed.
 * @property weatherData The current weather data to display, or null if unavailable.
 * @property forecastData The forecast weather data to display, or null if unavailable.
 * @property errorMessage An optional error message for current weather loading failures, or null if no error.
 * @property isForecastLoading Indicates whether the forecast data is being loaded.
 * @property forecastErrorMessage An optional error message for forecast loading failures, or null if no error.
 */
data class WeatherUIState(
    val isInitialLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val weatherData: WeatherData? = null,
    val forecastData: ForecastData? = null,
    val errorMessage: Int? = null,
    val isForecastLoading: Boolean = false,
    val forecastErrorMessage: String? = null
)
