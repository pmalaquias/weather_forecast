package com.pmalaquias.weatherforecast.data.remote.dto

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for accessing weather data from the remote Weather API.
 *
 * Provides methods to retrieve current weather information.
 */
interface WeatherApiService {
    @GET("current.json") // Endpoint for current weather data
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") locationQuery: String, // Format "latitude,longitude"
        @Query("lang") lang: String = "pt" // Optional: for Portuguese
        // Add @Query("aqi") aqi: String = "no" if you don't want air quality data
    ): Response<WeatherApiResponseDto> // Returns a Response to check the success of the call

    // Add other API methods as needed
}