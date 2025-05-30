package com.pmalaquias.weatherforecast.data.repositories

import android.util.Log
import com.pmalaquias.weatherforecast.BuildConfig
import com.pmalaquias.weatherforecast.data.local.LocationProvider
import com.pmalaquias.weatherforecast.data.remote.RetrofitClient
import com.pmalaquias.weatherforecast.data.remote.dto.WeatherApiResponseDto
import com.pmalaquias.weatherforecast.data.remote.dto.WeatherApiService
import com.pmalaquias.weatherforecast.domain.models.CurrentWeather
import com.pmalaquias.weatherforecast.domain.models.LocationInfo
import com.pmalaquias.weatherforecast.domain.models.WeatherCondition
import com.pmalaquias.weatherforecast.domain.models.WeatherData
import com.pmalaquias.weatherforecast.domain.repository.WeatherRepository


/**
 * Weather data repository implementation.
 *
 * This class is responsible for fetching current weather data from a remote API,
 * using the device's current location. It uses a `LocationProvider` to obtain
 * the location and a `WeatherApiService` to perform API calls.
 *
 * Main responsibilities:
 * - Obtain the device's current location.
 * - Make weather API calls using Retrofit.
 * - Map API DTOs to the application's domain models.
 * - Handle network errors, unavailable location, and invalid API responses.
 *
 * @property locationProvider Location provider injected via constructor.
 * @property weatherApiService Weather API service, ideally injected (e.g., via Hilt).
 *
 * Main methods:
 * - `getCurrentWeatherFromApi()`: Fetches current weather directly from the API, returning the DTO.
 * - `getCurrentWeatherData()`: Fetches current weather and returns the domain model `WeatherData`.
 * - `mapDtoToDomain(dto: WeatherApiResponseDto)`: Maps the API response DTO to the domain model.
 */
class WeatherRepositoryImpl(
    private val locationProvider: LocationProvider,
    private val weatherApiService: WeatherApiService = RetrofitClient.instance, // Ideally injected (e.g., via Hilt)
)  : WeatherRepository  { // Implement your WeatherRepository interface

    private val TAG = "WeatherRepositoryImpl"

    // Define your domain models (e.g., WeatherData, CurrentWeather, LocationInfo)
    // and a Result class (e.g., sealed class Result<out T> { data class Success ... data class Error ... })

    suspend fun getCurrentWeatherFromApi(): /* Result<WeatherData> */ WeatherApiResponseDto? { // Change to your Result<WeatherData>
        try {
            val location = locationProvider.getCurrentLocation()
            if (location != null) {
                val latLonQuery = "${location.latitude},${location.longitude}"
                Log.d("WeatherRepository", "Fetching weather for: $latLonQuery")

                val response = weatherApiService.getCurrentWeather(
                    apiKey = BuildConfig.WEATHER_API_KEY, // from BuildConfig
                    locationQuery = latLonQuery,
                    lang = "pt"
                )

                if (response.isSuccessful) {
                    val weatherDataDto = response.body()
                    Log.d("WeatherRepository", "API Response: $weatherDataDto")
                    // Here you would map weatherDataDto to your domain model WeatherData
                    // return Result.Success(mappedWeatherData)
                    return weatherDataDto // Retornando DTO diretamente para este exemplo simples
                } else {
                    Log.e("WeatherRepository", "API Error: ${response.code()} - ${response.message()}")
                    // return Result.Error("API Error: ${response.code()}")
                    return null
                }
            } else {
                Log.e("WeatherRepository", "Location not available")
                // return Result.Error("Location not available")
                return null
            }
        } catch (e: Exception) {
            Log.e("WeatherRepository", "Network Exception: ${e.message}", e)
            // return Result.Error("Network Exception: ${e.message}")
            return null
        }
    }

    override suspend fun getCurrentWeatherData(): WeatherData? {
        try {
            // 1. get location from LocationProvider
            val deviceLocation = locationProvider.getCurrentLocation()

            // 2. Check if location is available
            if (deviceLocation == null) {
                Log.e(TAG, "Device location not available.")
                return null // Return null if location can't be obtained
            }

            // 3. Format location query for API
            val latLonQuery = "${deviceLocation.latitude},${deviceLocation.longitude}"
            Log.d(TAG, "Fetching weather for location: $latLonQuery") // Log for debugging

            // 4. Make API Call
            val response = weatherApiService.getCurrentWeather(
                apiKey = BuildConfig.WEATHER_API_KEY, // Your API key from BuildConfig
                locationQuery = latLonQuery,
                lang = "pt" // Set response language to Portuguese
            )

            // 5. Check API Response
            if (response.isSuccessful) {
                val weatherDto = response.body() // Get response body (the DTO)
                if (weatherDto != null) {
                    Log.i(TAG, "API Response successful: ${weatherDto.location.name}")
                    // 6. Map DTO to Domain Model and Return
                    return mapDtoToDomain(weatherDto)
                } else {
                    Log.e(TAG, "API Response body is null.")
                    return null // Empty response body, something went wrong
                }
            } else {
                // API Error (e.g., 401 - Unauthorized, 404 - Not Found, etc.)
                Log.e(TAG, "API Error: ${response.code()} - ${response.message()}")
                return null
            }
        } catch (e: Exception) {
            // 7. Handle General Exceptions (e.g., network error, JSON parsing issue)
            Log.e(TAG, "Exception fetching weather data: ${e.message}", e)
            return null
        }
    }

    /**
     * Maps a [WeatherApiResponseDto] data transfer object to the domain model [WeatherData].
     *
     * This function performs the following mappings:
     * - Converts [LocationDto] to [LocationInfo], extracting location details such as name, region, country, and local time.
     * - Converts [ConditionDto] to [WeatherCondition], ensuring the icon URL is properly prefixed with "https:".
     * - Converts [CurrentWeatherDto] to [CurrentWeather], including temperature, wind speed, humidity, feels-like temperature, and day/night status.
     * - Combines the mapped location and current weather data into a [WeatherData] object.
     *
     * @param dto The [WeatherApiResponseDto] received from the API.
     * @return The mapped [WeatherData] domain model.
     */
    private fun mapDtoToDomain(dto: WeatherApiResponseDto): WeatherData {
        // Map LocationDto to LocationInfo (your domain model)
        val locationInfo = LocationInfo(
            name = dto.location.name,
            region = dto.location.region,
            country = dto.location.country,
            localtime = dto.location.localtime
        )

        // Map ConditionDto to WeatherCondition
        val weatherCondition = WeatherCondition(
            text = dto.current.condition.text,
            // Adds "https:" to the icon URL, since the API does not include it
            iconUrl = "https:${dto.current.condition.iconUrl}"
        )

        // Map CurrentWeatherDto to CurrentWeather
        val currentWeather = CurrentWeather(
            tempCelcius = dto.current.tempCelcius,
            condition = weatherCondition, // Use the mapped WeatherCondition
            windKph = dto.current.windKph,
            humidity = dto.current.humidity,
            feelslikeCelcius = dto.current.feelslikeCelcius,
            isDay = dto.current.isDay
        )

        // Create and return the final WeatherData object with the mapped data
        return WeatherData(
            location = locationInfo,
            current = currentWeather
        )
    }
}