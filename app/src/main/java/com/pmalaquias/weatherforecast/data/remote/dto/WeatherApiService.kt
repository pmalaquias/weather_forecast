package com.pmalaquias.weatherforecast.data.remote.dto

import com.pmalaquias.weatherforecast.data.remote.dto.SearchLocationDto
import com.pmalaquias.weatherforecast.data.remote.dto.forecast.ForecastApiResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * WeatherApiService provides methods to access weather data from a remote API.
 *
 * Available methods:
 * - getCurrentWeather: Retrieves current weather information for a specific location.
 *   Parameters:
 *     @param apiKey API access key.
 *     @param locationQuery Location in the format "latitude,longitude" or city name.
 *     @param lang (Optional) Response language, default is "pt" (Portuguese).
 *   Returns: Response containing current weather data.
 *
 * - getForecastWeather: Retrieves weather forecast for a specific location.
 *   Parameters:
 *     @param apiKey API access key.
 *     @param locationQuery Location in the format "latitude,longitude" or city name.
 *     @param days (Optional) Number of days for the forecast, default is 7.
 *     @param lang (Optional) Response language, default is "pt" (Portuguese).
 *   Returns: Response containing weather forecast data.
 *
 * Note: Add other methods as needed to access different API endpoints.
 */
interface WeatherApiService {
    @GET("current.json") // Endpoint for current weather data
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,
        @Query("q") locationQuery: String, // Format "latitude,longitude"
        @Query("lang") lang: String = "pt" // Optional: for Portuguese
        // Add @Query("aqi") aqi: String = "no" if you don't want air quality data
    ): Response<WeatherApiResponseDto> // Returns a Response to check the success of the call


    @GET("forecast.json") // Endpoint for weather forecast data
    suspend fun  getForecastWeather(
        @Query("key") apiKey: String,
        @Query("q") locationQuery: String, // Format "latitude,longitude"
        @Query("days") days: Int = 7, // Default to 7 days forecast
        @Query("lang") lang: String = "pt" // Optional: for Portuguese
    ): Response<ForecastApiResponseDto>


    @GET("search.json")
    suspend fun searchCity(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): Response<List<SearchLocationDto>> 
    // Add other API methods as needed
}