package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import com.pmalaquias.weatherforecast.domain.models.WeatherData

/**
 * Represents the UI state for the weather screen.
 *
 * @property isLoading Indicates whether the weather data is currently being loaded.
 * @property weatherData The weather data to be displayed, or null if not available.
 * @property errorMessage An optional error message to display if loading fails, or null if there is no error.
 */
data class WeatherUIState(
    val isLoading: Boolean = false,
    val weatherData: WeatherData? = null,
    val errorMessage: String? = null
)
