package com.pmalaquias.weatherforecast.domain.repository

import com.pmalaquias.weatherforecast.domain.models.WeatherData

/**
 * Repository interface for accessing weather data.
 */
interface WeatherRepository {
    suspend fun getCurrentWeatherData(): WeatherData?
}