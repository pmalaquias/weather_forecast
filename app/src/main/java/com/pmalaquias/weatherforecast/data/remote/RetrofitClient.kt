package com.pmalaquias.weatherforecast.data.remote

import com.pmalaquias.weatherforecast.data.remote.dto.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton object responsible for providing a configured instance of [com.pmalaquias.weatherforecast.data.remote.dto.WeatherApiService]
 * using Retrofit. The Retrofit instance is initialized lazily with the base URL for the
 * Weather API and a Gson converter for JSON serialization/deserialization.
 *
 * @property BASE_URL The base URL for the Weather API endpoints.
 * @property instance Lazily initialized [com.pmalaquias.weatherforecast.data.remote.dto.WeatherApiService] implementation for making API calls.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.weatherapi.com/v1/" // Base URL for the Weather API

    val instance: WeatherApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // Set the base URL for the API
            .addConverterFactory(GsonConverterFactory.create()) // Gson converter
            .build()
        retrofit.create(WeatherApiService::class.java)
    }
}